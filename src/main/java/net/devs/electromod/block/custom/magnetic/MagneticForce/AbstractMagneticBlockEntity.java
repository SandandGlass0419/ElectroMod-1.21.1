package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractMagneticBlockEntity extends BlockEntity
{
    private int magneticForce = 0;

    public void setMagneticForce(int magneticForce)
    {
        if (this.magneticForce == magneticForce) return;

        this.magneticForce = magneticForce;
        markDirty();

        // put updates
    }

    public int getMagneticForce() { return magneticForce; }

    public AbstractMagneticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void blockentityLoaded(World world)
    {
        MagneticForceInteractor.subscribeMagneticBlock(world, this.getPos(), magneticForce);
    }

    public void blockentityUnloaded(World world)
    {
        MagneticForceInteractor.unsubscribeMagneticBlock(world, this.getPos());
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("magnetic_force", magneticForce);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
        nbt.getInt("magnetic_force");
    }
}
