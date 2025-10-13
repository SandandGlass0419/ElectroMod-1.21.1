package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public abstract class AbstractMagneticBlockEntity extends BlockEntity
{
    public AbstractMagneticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void blockentityLoaded(World world, BlockEntity forceBE)
    {
        MagneticForceInteractor.subscribeBlock(getDimensionKey(world), forceBE.getPos());
    }

    public void blockentityUnloaded(World world, BlockEntity forceBE)
    {
        MagneticForceInteractor.unsubscribeBlock(getDimensionKey(world), forceBE.getPos());
    }

    public RegistryKey<DimensionType> getDimensionKey(World world)
    {
        var key = world.getDimensionEntry().getKey();
        return key.orElse(null);
    }
}
