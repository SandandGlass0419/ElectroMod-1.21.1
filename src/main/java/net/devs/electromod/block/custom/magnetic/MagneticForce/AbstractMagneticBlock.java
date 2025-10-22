package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.block.entity.custom.magnetic.CoilBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractMagneticBlock extends BlockWithEntity implements BlockEntityProvider
{
    public static final DirectionProperty FACING = Properties.FACING;

    public AbstractMagneticBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
    {
        if (!(world.getBlockEntity(pos) instanceof CoilBlockEntity)) return ActionResult.FAIL;

        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof DebugStickItem)
        {
            if (world.isClient()) return ActionResult.FAIL;

            testMagneticPower(world, pos, player);
            testMagneticField(world, pos);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private void testMagneticPower(World world, BlockPos pos, PlayerEntity player)
    {
        if (world.getBlockEntity(pos) instanceof CoilBlockEntity coilBE)
        {
            player.sendMessage(Text.literal(coilBE.getMagneticPower() + "," + coilBE.getRedstoneInput() + "," + coilBE.getFacing().toString()), true);
        }
    }

    private void testMagneticField(World world, BlockPos pos)
    {
        if (world.isClient()) return;
        if (!(world.getBlockEntity(pos) instanceof AbstractMagneticBlockEntity)) return;

        ForceProfile profile = MagneticForceInteractor.getForceProfile(MagneticForceInteractor.getField(world, pos));

        BlockState[] forceBlocks = {
                Blocks.RED_STAINED_GLASS.getDefaultState(),
                Blocks.CYAN_STAINED_GLASS.getDefaultState(),
                Blocks.LIGHT_BLUE_STAINED_GLASS.getDefaultState()
        };

        for (var headSet : profile.headProfile())
        {
            for (var mvec : headSet)
            {
                world.setBlockState(new BlockPos(mvec.add(pos)),
                        forceBlocks[Math.abs(mvec.getPowerDelta())]);
            }
        }

        for (var bodySet : profile.bodyProfile())
        {
            for (var mvec : bodySet)
            {
                world.setBlockState(new BlockPos(mvec.add(pos)),
                        forceBlocks[Math.abs(mvec.getPowerDelta())]);
            }
        }

        for (var tailSet : profile.tailProfile())
        {
            for (var mvec : tailSet)
            {
                world.setBlockState(new BlockPos(mvec.add(pos)),
                        forceBlocks[Math.abs(mvec.getPowerDelta())]);
            }
        }
    }
}
