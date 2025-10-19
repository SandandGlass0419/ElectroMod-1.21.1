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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PNDiode extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    public PNDiode(Settings settings) {
        super(settings);
        // 기본 방향 NORTH
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        Direction side = ctx.getSide();

        // 바닥/천장 설치 시 플레이어 수평 방향 기준
        if (side == Direction.UP || side == Direction.DOWN) {
            if (player != null) {
                return this.getDefaultState().with(FACING, player.getHorizontalFacing().getOpposite());
            } else {
                return this.getDefaultState().with(FACING, Direction.NORTH);
            }
        }

        // 벽에 설치 시 클릭한 면 방향
        return this.getDefaultState().with(FACING, side);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        if (world.isClient) return;

        Direction facing = state.get(FACING);

        // FACING 기준 전류 체크 및 전달할 방향 결정
        Direction checkDir;    // 전류를 확인할 방향
        Direction sendDir;     // 전류를 보낼 방향

        switch (facing) {
            case SOUTH -> { checkDir = Direction.EAST; sendDir = Direction.WEST; }
            case WEST  -> { checkDir = Direction.SOUTH; sendDir = Direction.NORTH; }
            case NORTH -> { checkDir = Direction.WEST; sendDir = Direction.EAST; }
            case EAST  -> { checkDir = Direction.NORTH; sendDir = Direction.SOUTH; }
            default -> { return; } // UP/DOWN은 처리하지 않음
        }

        BlockPos checkPos = pos.offset(checkDir);
        BlockPos sendPos = pos.offset(sendDir);

        BlockEntity checkBE = world.getBlockEntity(checkPos);
        BlockEntity sendBE = world.getBlockEntity(sendPos);

        if (checkBE instanceof WireBlockEntity sourceWire && sendBE instanceof WireBlockEntity targetWire) {
            float value = sourceWire.getElectrocity();
            if (value > 0f) {
                // targetWire에 전류 전달 (setElectrocity 사용)
                BlockState targetState = world.getBlockState(sendPos);
                targetWire.setElectrocity(value, world, sendPos, targetState, targetWire);
            }
        }
    }

}
