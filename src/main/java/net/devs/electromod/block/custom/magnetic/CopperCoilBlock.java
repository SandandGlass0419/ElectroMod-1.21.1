package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.custom.magnetic.force.ForceProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperCoilBlock extends CoilBlock
{
    public static final MapCodec<CopperCoilBlock> CODEC = CopperCoilBlock.createCodec(CopperCoilBlock::new);

    public CopperCoilBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    // block features
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
    {
        var returned = super.onUse(state, world, pos, player, hit);
        if (returned != ActionResult.PASS) return returned;

        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;

        // default action
        world.setBlockState(pos, state.cycle(DENSITY), Block.NOTIFY_ALL);
        world.playSound(null, pos, SoundEvents.BLOCK_COPPER_GRATE_HIT, SoundCategory.BLOCKS);

        return ActionResult.success(world.isClient);
    }

    // magnetic force features
    public static final int copperAdditiveFactor = Math.abs(ForceProfile.powerCategory.COPPER.get()) * 15 * DENSITY_MAX;

    @Override
    public int defaultForceFormula(int redstonePower, int density)
    {
        return redstonePower == 0 ? 0 : redstonePower * density + copperAdditiveFactor;
    }

    @Override
    public float defaultForceFormula(int magneticPower, int density, float tickDiff)
    {
        return magneticPower == 0 ? 0 : density * magneticPower / tickDiff + copperAdditiveFactor;
    }
}