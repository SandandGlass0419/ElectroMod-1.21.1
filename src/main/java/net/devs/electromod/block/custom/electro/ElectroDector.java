package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElectroDector extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);

    public ElectroDector(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) { return SHAPE; }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;

        if (player.getMainHandStack().getItem() instanceof DebugStickItem)
        {
            if (world.isClient()) return ActionResult.FAIL;
            player.sendMessage(Text.literal("I am herby the Electro Dector, the king of all detectors!"));
        }

        double maxElectricity = Double.NEGATIVE_INFINITY;

        // 여섯 방향 (UP, DOWN, NORTH, SOUTH, EAST, WEST) 탐색
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);

            if (world.getBlockEntity(targetPos) instanceof WireBlockEntity wireBE) {
                maxElectricity = Math.max(wireBE.getElectricity(), maxElectricity);
            }
        }

        if (maxElectricity != Double.NEGATIVE_INFINITY) {
            player.sendMessage(Text.literal("Detected electricity: " + maxElectricity), true);
        } else {
            player.sendMessage(Text.literal("No powered Wires nearby."), true);
        }

        return ActionResult.success(world.isClient());
    }

    public static final double explosionThreshold = 150;

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) return;

        if (!(world.getBlockEntity(sourcePos) instanceof WireBlockEntity wireBE)) return;

        double electricity = wireBE.getElectricity();


        if (electricity > explosionThreshold) {
            // 전류량에 비례해 폭발
            float explosionPower = (float) (electricity - explosionThreshold) / 2; // 필요시 스케일링 가능
            world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, explosionPower, World.ExplosionSourceType.BLOCK);
            world.removeBlock(pos, false);
        }
    }
}
