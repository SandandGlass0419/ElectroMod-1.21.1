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

    // ⚡ 공통 처리 로직 (onPlaced와 neighborUpdate 둘 다에서 사용)
    private void electrifyNearbyWires(World world, BlockPos pos) {
        if (world.isClient) return;

        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};

        for (Direction dir : directions) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof WireBlock) {
                if (world.getBlockEntity(neighborPos) instanceof WireBlockEntity wireBE) {
                    wireBE.setElectrocity(15f, world, neighborPos, neighborState, wireBE);
                }
            }
        }
    }
    // 🔹 설치 시 전기 공급
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        electrifyNearbyWires(world, pos);
    }

    // 🔹 주변 블록 변경 시에도 전기 공급 유지
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        electrifyNearbyWires(world, pos);
    }
}
