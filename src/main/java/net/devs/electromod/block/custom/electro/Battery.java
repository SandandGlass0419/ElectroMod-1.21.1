package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Battery extends Block {
    public static final DirectionProperty FACING = Properties.FACING;

    public Battery(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    // ⚡ 주변 Wire 전류 변경 (설치 시 or 업데이트 시)
    private void setNearbyWireElectrocity(World world, BlockPos pos, float value) {
        if (world.isClient) return;

        Direction[] directions = {
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST,
                Direction.UP, Direction.DOWN
        };

        for (Direction dir : directions) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState wireState = world.getBlockState(neighborPos);
            Block neighborBlock = wireState.getBlock();

            if (neighborBlock instanceof WireBlock) {
                if (world.getBlockEntity(neighborPos) instanceof WireBlockEntity wireBE) {
                    wireBE.updateElectricity(value);
                }
            }
        }
    }

    // 🔋 설치 시 전기 공급
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        setNearbyWireElectrocity(world, pos, 15f);
    }

    // 🔁 주변 변경 시 전류 유지
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        setNearbyWireElectrocity(world, pos, 15f);
    }

    // ❌ 파괴 시 전류 차단
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            clearConnectedWires(world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    // 🧠 연결된 모든 Wire 탐색 및 전류 0으로 초기화
    private void clearConnectedWires(World world, BlockPos startPos) {
        if (world.isClient) return;

        HashSet<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        // 1️⃣ 처음엔 Battery 주변 6방향에서 시작
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = startPos.offset(dir);
            if (world.getBlockState(neighbor).getBlock() instanceof WireBlock) {
                queue.add(neighbor);
            }
        }

        // 2️⃣ BFS 탐색으로 연결된 Wire 전체 탐색
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;

            BlockState currentState = world.getBlockState(current);
            Block currentBlock = currentState.getBlock();

            if (currentBlock instanceof WireBlock) {
                if (world.getBlockEntity(current) instanceof WireBlockEntity wireBE) {
                    // 현재 전류가 이미 0이면 스킵 (불필요한 반복 방지)
                    if (wireBE.getElectricity() > 0f) {
                        wireBE.setElectricity(0f);
                    }

                    // 연결된 Wire 계속 탐색
                    for (Direction dir : Direction.values()) {
                        BlockPos next = current.offset(dir);
                        if (!visited.contains(next)
                                && world.getBlockState(next).getBlock() instanceof WireBlock) {
                            queue.add(next);
                        }
                    }
                }
            }
        }
    }
}
