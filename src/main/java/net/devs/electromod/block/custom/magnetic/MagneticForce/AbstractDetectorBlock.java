package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractDetectorBlock extends BlockWithEntity
{
    protected AbstractDetectorBlock(Settings settings)
    {
        super(settings);
    }

    public final void tick(World world1, BlockPos pos, BlockState state1, BlockEntity blockEntity)
    {
        if (!(blockEntity instanceof AbstractDetectorBlockEntity detectorBE)) return;
        if (!detectorBE.getStartWatch()) return;

        watchIntick(world1, pos, state1, detectorBE);
    }

    public abstract void watchIntick(World world1, BlockPos pos, BlockState state1, AbstractDetectorBlockEntity abstractBE);
}
