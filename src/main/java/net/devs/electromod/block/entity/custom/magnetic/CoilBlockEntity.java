package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.CoilBlock;
import net.devs.electromod.block.custom.magnetic.MagneticForce.AbstractMagneticBlockEntity;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class CoilBlockEntity extends AbstractMagneticBlockEntity
{
    private int redstoneInput = 0;

    private long lastUsedTick = 0;
    private boolean isTicking = false;

    public void setRedstoneInput(int power)
    {
        if (this.redstoneInput != power)
        {
            this.redstoneInput = power;
            markDirty();
        }
    }

    public int getRedstoneInput() { return this.redstoneInput; }

    public void setLastUsedTick(long lastUsedTick) { this.lastUsedTick = lastUsedTick; }

    public float getTickDiff(long usedTick)
    {
        return usedTick == this.lastUsedTick ? CoilBlock.minMargin : usedTick - this.lastUsedTick;
    }

    public void setIsTicking(boolean isTicking) { this.isTicking = isTicking; }
    public boolean getIsTicking() { return this.isTicking; }

    public CoilBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.COIL_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("redstone_input", this.redstoneInput);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
        this.redstoneInput = nbt.getInt("redstone_input");
    }
}