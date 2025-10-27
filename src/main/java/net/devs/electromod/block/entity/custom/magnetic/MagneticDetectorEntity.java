package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.MagneticDetector;
import net.devs.electromod.block.custom.magnetic.force.AbstractDetectorBlockEntity;
import net.devs.electromod.block.custom.magnetic.force.MagneticField;
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
    private int detectionAxisID;

    public MagneticDetectorEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.MAGNETIC_DETECTOR_BE, pos, state);

        this.detectionAxisID = MagneticDetector.getDetectionAxis(state).ordinal();
        markDirty();
    }

    public void setRedstoneOutput(int power)
    {
        if (this.world == null || this.world.isClient()) return;

        if (this.redstoneOutput != power)
        {
            this.redstoneOutput = power;
            markDirty();
            this.world.updateNeighborsAlways(this.pos, getCachedState().getBlock());
        }
    }

    public int getRedstoneOutput() { return this.redstoneOutput; }

    @Override
    public boolean additionalConditions(MagneticField field)
    {
        if (!super.additionalConditions(field)) return false;

        return this.detectionAxisID == field.getForceDirection().getAxis().ordinal();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("redstone_output", this.redstoneOutput);
        nbt.putInt("axis_id", this.detectionAxisID);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
        this.redstoneOutput = nbt.getInt("redstone_output");
        this.detectionAxisID = nbt.getInt("axis_id");
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }
}