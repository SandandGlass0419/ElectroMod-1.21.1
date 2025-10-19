package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractDetectorBlockEntity extends BlockEntity
{
    public AbstractDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void blockentityLoaded()
    {
        BlockField validPoses = MagneticForceInteractor.detectorPlacementCheck(this.world, this.pos);
        MagneticForceInteractor.subscribeDetectorBlock(this.world, this.pos, validPoses);
    }

    public void blockentityUnloaded()
    {
        MagneticForceInteractor.unsubscribeDetectorBlock(this.world, this.getPos());
    }
}
