package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.MagneticDetector;
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

public class MagneticDetectorEntity extends BlockEntity
{
    private int redstoneOutput = 0;
    private boolean needsUpdate = true;

    public MagneticDetectorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DETECTOR_BE, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        if (world.isClient()) return;

        if (needsUpdate)
        {
            setRedstoneOutput(MagneticDetector.defaultPowerFormula(state));
            needsUpdate = false;
        }

    }

    public void setRedstoneOutput(int power)
    {
        if (world == null || world.isClient()) return;

        if (redstoneOutput != power)
        {
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
        nbt.getInt("redstone_output");
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