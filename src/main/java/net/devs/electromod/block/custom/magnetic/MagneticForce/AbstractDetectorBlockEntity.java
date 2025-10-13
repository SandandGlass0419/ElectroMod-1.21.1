package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public abstract class AbstractDetectorBlockEntity extends BlockEntity
{
    public AbstractDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void blockentityLoaded(World world, BlockEntity detectorBE)
    {
        Set<BlockPos> validPoses = MagneticForceInteractor.detectorPlacementCheck(world, detectorBE.getPos());
        MagneticForceInteractor.subscribeDetectorBlock(world, detectorBE.getPos(), validPoses);
    }

    public void blockentityUnloaded(World world, BlockEntity detectorBE)
    {
        MagneticForceInteractor.unsubscribeDetectorBlock(world, detectorBE.getPos());
    }
}
