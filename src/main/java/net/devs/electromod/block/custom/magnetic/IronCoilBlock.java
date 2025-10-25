package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.components.ModDataComponentTypes;
import net.devs.electromod.item.custom.magnetic.MagnetItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IronCoilBlock extends CoilBlock
{
    public static final MapCodec<IronCoilBlock> CODEC = IronCoilBlock.createCodec(IronCoilBlock::new);

    public IronCoilBlock(Settings settings)
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
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof MagnetItem) // custom action
        {
            if (world.isClient()) return ActionResult.FAIL;

            int magnet_force = stack.getOrDefault(ModDataComponentTypes.MAGNETIC_POWER, ModDataComponentTypes.min_power);
            player.sendMessage(Text.literal("Current force: " + magnet_force), true);

            return ActionResult.SUCCESS;
        }

        else // default action
        {
            BlockState newBlockState = state.cycle(DENSITY);

            world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.BLOCKS);


            return ActionResult.success(world.isClient);
        }
    }

    // magnetic force features
    public static final int ironAdditiveFactor = 0 * 15 * DENSITY_MAX;

    @Override
    public int defaultForceFormula(int redstonePower, int density)
    {
        return redstonePower == 0 ? 0 : redstonePower * density + ironAdditiveFactor;
    }

    @Override
    public int defaultForceFormula(int magneticPower, int density, Double diff)
    {
        return 3;
    }
}