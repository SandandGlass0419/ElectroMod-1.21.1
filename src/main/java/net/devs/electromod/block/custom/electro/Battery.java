package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Battery extends Block {
    public static final DirectionProperty FACING = Properties.FACING;

    public Battery(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP));
    }

    // 설치 방향 설정: 플레이어가 바라보는 방향을 앞면으로
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }



    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        // 1틱 후 scheduledTick 호출
        world.scheduleBlockTick(pos, this, 1);
    }

    // scheduledTick: FACING 방향에 WireBlock 있으면 전류 1로 설정
    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        Direction facing = state.get(FACING);
        BlockPos targetPos = pos.offset(facing);
        BlockState targetState = world.getBlockState(targetPos);

        if (targetState.getBlock() instanceof net.devs.electromod.block.custom.electro.WireBlock) {
            if (world.getBlockEntity(targetPos) instanceof WireBlockEntity wireBE) {
                wireBE.setElectrocity(10f); // 전류 1로 설정
            }
        }
    }
}
