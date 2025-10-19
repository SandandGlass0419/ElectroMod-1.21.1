package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElectroDector extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

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
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            double maxElectrocity = Double.NEGATIVE_INFINITY;
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();

            int range = 1; // 탐색 범위 반경 5블록

            for (int x = -range; x <= range; x++) {
                for (int y = -range; y <= range; y++) {
                    for (int z = -range; z <= range; z++) {
                        mutablePos.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        BlockEntity blockEntity = world.getBlockEntity(mutablePos);
                        if (blockEntity instanceof WireBlockEntity wire) {
                            double e = wire.getElectrocity();
                            if (e > maxElectrocity) maxElectrocity = e;
                        }
                    }
                }
            }

            if (maxElectrocity != Double.NEGATIVE_INFINITY) {
                player.sendMessage(Text.literal("Electrocity: " + maxElectrocity), true);
            } else {
                player.sendMessage(Text.literal("No WireBlock nearby."), true);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        if (world.isClient) return;

        float maxElectrocity = 0f;
        boolean found = false;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        Direction[] horizontals = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        // 주변 Wire 최대 전류 찾기
        for (Direction dir : horizontals) {
            mutablePos.set(pos.offset(dir));
            BlockEntity be = world.getBlockEntity(mutablePos);
            if (be instanceof WireBlockEntity wire) {
                float e = wire.getElectrocity();
                if (!found || e > maxElectrocity) {
                    maxElectrocity = e;
                    found = true;
                }
            }
        }

        if (found) {
            // 주변 Wire에 최대 전류 설정 (setElectrocity 사용)
            for (Direction dir : horizontals) {
                mutablePos.set(pos.offset(dir));
                BlockEntity be = world.getBlockEntity(mutablePos);
                if (be instanceof WireBlockEntity wire) {
                    // state는 현재 BlockState
                    BlockState wireState = world.getBlockState(mutablePos);
                    wire.setElectrocity(maxElectrocity, world, mutablePos, wireState, wire);
                }
            }
        }
    }





}
