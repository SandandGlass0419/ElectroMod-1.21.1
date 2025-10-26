package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.ElectroMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMagneticBlockEntity extends BlockEntity
{
    private static final int defaultPower = 0;
    private int magneticPower = defaultPower;
    private int facingID;

    public void setMagneticPower(int magneticPower)
    {
        if (this.magneticPower == magneticPower) return;

        this.magneticPower = magneticPower;
        markDirty();

        onMagneticForceUpdate(magneticPower);
    }

    public int getMagneticPower() { return magneticPower; }

    public Direction getFacing() { return Direction.byId(facingID); }

    public AbstractMagneticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        this.facingID = state.get(Properties.FACING).getId();
        markDirty();
    }

    public void blockentityLoaded()
    {
        if (this.magneticPower == defaultPower) {
            ElectroMod.LOGGER.info("is default"); return; }    // only runs on world loaded to load existing be

        MagneticForceInteractor.subscribeMagneticBlock(this.world, this.pos, new MagneticField(this.magneticPower, this.getFacing()));
    }

    public void blockentityUnloaded()
    {
        MagneticForceInteractor.unsubscribeMagneticBlock(this.world, this.pos);
    }

    protected void onMagneticForceUpdate(int magneticPower)
    {
        if (magneticPower > defaultPower)
        {
            MagneticForceInteractor.subscribeMagneticBlock(this.world, this.pos, new MagneticField(magneticPower, this.getFacing()));
        }

        else
        {
            MagneticForceInteractor.unsubscribeMagneticBlock(world, this.pos);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("magnetic_force", magneticPower);
        nbt.putInt("facing_id", facingID);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
        this.magneticPower = nbt.getInt("magnetic_force");
        this.facingID = nbt.getInt("facing_id");
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }
}
