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

    public void blockentityLoaded(World world)
    {
        Set<BlockPos> validPoses = MagneticForceInteractor.detectorPlacementCheck(world, this.getPos());
        MagneticForceInteractor.subscribeDetectorBlock(world, this.getPos(), validPoses);
    }

    public void blockentityUnloaded(World world)
    {
        MagneticForceInteractor.unsubscribeDetectorBlock(world, this.getPos());
    }
}
