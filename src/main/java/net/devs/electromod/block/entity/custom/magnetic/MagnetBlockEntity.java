package net.devs.electromod.block.entity.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.MagnetBlock;
import net.devs.electromod.block.custom.magnetic.force.AbstractMagneticBlockEntity;
import net.devs.electromod.block.custom.magnetic.force.MagneticField;
import net.devs.electromod.block.custom.magnetic.force.MagneticForceInteractor;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class MagnetBlockEntity extends AbstractMagneticBlockEntity
{
    public MagnetBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.MAGNET_BE, pos, state);
    }

    @Override
    public void blockentityLoaded()
    {
        // no criteria since magnetic power is static

        setMagneticPower(MagnetBlock.MAGNET_POWER_IDENT);
        MagneticForceInteractor.subscribeMagneticBlock(this.world, this.pos, new MagneticField(MagnetBlock.MAGNET_POWER_IDENT, this.getFacing()));
    }
}
