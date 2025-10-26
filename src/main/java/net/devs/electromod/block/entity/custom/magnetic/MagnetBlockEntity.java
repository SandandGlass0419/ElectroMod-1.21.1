package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.MagneticForce.AbstractMagneticBlockEntity;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class MagnetBlockEntity extends AbstractMagneticBlockEntity
{
    public MagnetBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.MAGNET_BE, pos, state);
    }
}
