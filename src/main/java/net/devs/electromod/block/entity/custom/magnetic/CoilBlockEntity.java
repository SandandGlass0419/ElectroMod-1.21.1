package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.CoilBlock;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CoilBlockEntity extends BlockEntity
{
    private int redstoneInput = 0;
    private boolean needsUpdate = true;

    public CoilBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.COIL_BE, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if (world.isClient()) return;

        if (needsUpdate) // keep eye on
        {
            setRedstoneInput(CoilBlock.getRecievedRedstonePower(world, pos, state));
            needsUpdate = false;
        }
    }

    public void setRedstoneInput(int power)
    {
        if (this.redstoneInput != power)
        {
            this.redstoneInput = power;
            markDirty();
        }
    }

    public int getRedstoneInput() { return this.redstoneInput; }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("redstone_input", redstoneInput);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
        nbt.getInt("redstone_input");
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup)
    {
        return createNbt(registryLookup);
    }
}
