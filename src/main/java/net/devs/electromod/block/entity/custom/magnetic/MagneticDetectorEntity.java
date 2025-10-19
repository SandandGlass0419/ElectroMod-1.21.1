package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.MagneticForce.AbstractDetectorBlockEntity;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MagneticDetectorEntity extends AbstractDetectorBlockEntity
{
    private int redstoneOutput = 0;

    public MagneticDetectorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DETECTOR_BE, pos, state);
    }

    public void setRedstoneOutput(int power) {
        if (world == null || world.isClient()) return;

        if (redstoneOutput != power) {
            this.redstoneOutput = power;
            markDirty();
            world.updateNeighborsAlways(pos, getCachedState().getBlock());
        }
    }

    public int getRedstoneOutput() { return this.redstoneOutput; }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("redstone_output", redstoneOutput);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
        this.redstoneOutput = nbt.getInt("redstone_output");
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }
}