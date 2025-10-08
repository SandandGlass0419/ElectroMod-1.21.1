package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

        if (!world.isClient()) { // 서버 측에서만 실행
            // Battery가 바라보는 방향
            Direction facing = state.get(FACING);
            BlockPos targetPos = pos.offset(facing); // Battery 앞쪽 위치
            BlockState targetState = world.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            // WireBlock을 상속받았는지 확인
            if (targetBlock instanceof WireBlock) {
                // BlockEntity 가져오기
                if (world.getBlockEntity(targetPos) instanceof WireBlockEntity wireEntity) {
                    // Electrocity 설정
                    wireEntity.setElectrocity(10);
                }
            }
        }
    }


}
