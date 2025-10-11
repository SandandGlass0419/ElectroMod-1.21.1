package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.custom.magnetic.MagneticForce.ForceProfile;
import net.devs.electromod.block.custom.magnetic.MagneticForce.MagneticForceInteractor;
import net.devs.electromod.block.entity.custom.magnetic.CoilBlockEntity;
import net.devs.electromod.components.ModDataComponentTypes;
import net.devs.electromod.item.custom.electro.ElectroStaff;
import net.devs.electromod.item.custom.magnetic.MagnetItem;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class IronCoilBlock extends CoilBlock implements BlockEntityProvider
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
        if (!(world.getBlockEntity(pos) instanceof CoilBlockEntity)) return ActionResult.FAIL;
        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof MagnetItem) // custom action
        {
            int magnet_force = stack.getOrDefault(ModDataComponentTypes.MAGNET_FORCE, ModDataComponentTypes.min_force);
            player.sendMessage(Text.literal("Current force: " + magnet_force), true);

            return ActionResult.SUCCESS;
        }

        else if (stack.getItem() instanceof ElectroStaff)
        {
            testMagneticField(world, pos, state);
            return ActionResult.SUCCESS;
        }

        else // default action
        {
            BlockState newBlockState = state.cycle(DENSITY);
            int newDensity = newBlockState.get(DENSITY);

            world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.BLOCKS);

            return ActionResult.success(world.isClient);
        }
    }

    private void testMagneticField(World world, BlockPos pos, BlockState state)
    {
        if (world.isClient()) return;

        int magneticForce = 15;
        int powerCategory = ForceProfile.getPowerCategory(15);
        Direction forceDirection = state.get(FACING);
        ForceProfile profile = ForceProfile.getForceProfile(powerCategory, forceDirection);

        var poses = MagneticForceInteractor.getAllPositions(pos, profile);

        BlockState[] forceBlocks = {
                Blocks.RED_STAINED_GLASS.getDefaultState(),
                Blocks.CYAN_STAINED_GLASS.getDefaultState(),
                Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState()
        };

        for (int i = 0; i < 3; i++)
        {
            for (BlockPos p : poses.get(i))
            {
                world.setBlockState(p, forceBlocks[i]);
            }
        }
    }
}