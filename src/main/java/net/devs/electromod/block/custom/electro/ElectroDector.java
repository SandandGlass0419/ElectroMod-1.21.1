package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
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
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            double maxElectro = Double.NEGATIVE_INFINITY;
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            int range = 1; // 탐색 범위 1블록

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockEntity be = world.getBlockEntity(mutablePos);
                        if (be instanceof WireBlockEntity wire) {
                            double e = wire.getElectricity();
                            if (e > maxElectro) maxElectro = e;
                        }
                    }
                }
            }

            if (maxElectro != Double.NEGATIVE_INFINITY) {
                player.sendMessage(Text.literal("Nearby WireBlock electricity: " + maxElectro), true);
            } else {
                player.sendMessage(Text.literal("No WireBlock nearby."), true);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        BlockEntity be = world.getBlockEntity(sourcePos);
        if (be instanceof WireBlockEntity wire) {
            double electricity = wire.getElectricity();
            if (electricity > 0) {
                // 전류량에 비례해 폭발
                float explosionPower = (float) electricity; // 필요시 스케일링 가능
                world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, explosionPower, World.ExplosionSourceType.BLOCK);
                world.removeBlock(pos, false);
            }
        }
    }

}
