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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PNDiode extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    // FACING별 VoxelShape (모델 기준 90도 회전 포함)
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 16);
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(0, 5, 5, 16, 11, 11);
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 5, 5, 16, 11, 11);
    private static final VoxelShape UP_SHAPE    = Block.createCuboidShape(5, 0, 5, 11, 16, 11);
    private static final VoxelShape DOWN_SHAPE  = Block.createCuboidShape(5, 0, 5, 11, 16, 11);


    public PNDiode(Settings settings) {
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> EAST_SHAPE;
            case SOUTH -> WEST_SHAPE;
            case EAST  -> NORTH_SHAPE;
            case WEST  -> SOUTH_SHAPE;
            case UP    -> UP_SHAPE;
            case DOWN  -> DOWN_SHAPE;
        };
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (world.isClient) return;

        Direction facing = state.get(FACING);

        Direction checkDir;
        Direction sendDir;

        switch (facing) {
            case SOUTH -> { checkDir = Direction.EAST; sendDir = Direction.WEST; }
            case WEST  -> { checkDir = Direction.SOUTH; sendDir = Direction.NORTH; }
            case NORTH -> { checkDir = Direction.WEST; sendDir = Direction.EAST; }
            case EAST  -> { checkDir = Direction.NORTH; sendDir = Direction.SOUTH; }
            default -> { return; }
        }

        BlockPos.Mutable checkPos = new BlockPos.Mutable();
        checkPos.set(pos.offset(checkDir));

        BlockPos.Mutable sendPos = new BlockPos.Mutable();
        sendPos.set(pos.offset(sendDir));

        // check 방향에서 Wire 탐색
        WireBlockEntity sourceWire = null;
        while (true) {
            BlockEntity be = world.getBlockEntity(checkPos);
            Block block = world.getBlockState(checkPos).getBlock();

            if (be instanceof WireBlockEntity wire) {
                sourceWire = wire;
                break;
            } else if (block instanceof PNDiode diode) {
                // FACING이 다르면 탐색 중단
                BlockState diodeState = world.getBlockState(checkPos);
                Direction diodeFacing = diodeState.get(FACING);
                if (diodeFacing != facing) break;
                checkPos.move(checkDir);
            } else if (block instanceof ElectroDector) {
                checkPos.move(checkDir);
            } else {
                break;
            }
        }

        if (sourceWire == null) return; // Wire 없으면 종료

        // send 방향에서 Wire 탐색
        WireBlockEntity targetWire = null;
        while (true) {
            BlockEntity be = world.getBlockEntity(sendPos);
            Block block = world.getBlockState(sendPos).getBlock();

            if (be instanceof WireBlockEntity wire) {
                targetWire = wire;
                break;
            } else if (block instanceof PNDiode diode) {
                BlockState diodeState = world.getBlockState(sendPos);
                Direction diodeFacing = diodeState.get(FACING);
                if (diodeFacing != facing) break;
                sendPos.move(sendDir);
            } else if (block instanceof ElectroDector) {
                sendPos.move(sendDir);
            } else {
                break;
            }
        }

        if (targetWire != null) {
            float value = sourceWire.getElectrocity();
            if (value > 0f) {
                BlockState targetState = world.getBlockState(sendPos);
                targetWire.setElectrocity(value, world, sendPos, targetState, targetWire);
            }
        }
    }



    // 클릭 시 FACING 반전 후 상태 갱신

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            Direction current = state.get(FACING);
            Direction opposite = current.getOpposite();
            world.setBlockState(pos, state.with(FACING, opposite), 3); // 3 = update client + notify neighbors
        }
        return ActionResult.SUCCESS;
    }
}
