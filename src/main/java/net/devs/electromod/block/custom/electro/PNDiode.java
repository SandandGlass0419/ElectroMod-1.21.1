package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.custom.electro.AcDcConvertEntity;
import net.devs.electromod.block.entity.custom.electro.PNDiodeEntity;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.jetbrains.annotations.Nullable;

import static net.devs.electromod.block.custom.electro.WireBlock.ELECTRIFIED;

public class PNDiode extends BlockWithEntity {


    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(5, 5, 0, 11, 11, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(5, 5, 0, 11, 11, 16); // Z 반전 필요
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0, 5, 5, 16, 11, 11);
    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0, 5, 5, 16, 11, 11);


    public static final DirectionProperty FACING = Properties.FACING;

    public PNDiode(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
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
        if (side == Direction.UP || side == Direction.DOWN) {
            return player != null
                    ? this.getDefaultState().with(FACING, player.getHorizontalFacing().getOpposite())
                    : this.getDefaultState().with(FACING, Direction.NORTH);
        }
        return this.getDefaultState().with(FACING, side);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PNDiodeEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            Direction current = state.get(FACING);
            Direction opposite = current.getOpposite();

            // 방향 전환
            world.setBlockState(pos, state.with(FACING, opposite), Block.NOTIFY_ALL);

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof PNDiodeEntity diodeEntity) {
                // 클릭 시 신호 초기화
                if (diodeEntity.getRedstonePower() != 0) {
                    diodeEntity.setRedstonePower(0);
                }

                // ✅ 방향 전환 후 즉시 전력 재검사
                BlockState newState = world.getBlockState(pos);
                neighborUpdate(newState, world, pos, this, pos, true);

                // ✅ 주변 블록들도 갱신되도록
                world.updateNeighborsAlways(pos, this);
            }
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof PNDiodeEntity diodeEntity)) return;

        Direction facing = state.get(FACING);
        Direction checkDir = switch (facing) {
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            default -> null;
        };
        if (checkDir == null) return;

        BlockPos checkPos = pos.offset(checkDir);
        int newPower = 0;

        while (true) {
            BlockState checkState = world.getBlockState(checkPos);
            BlockEntity checkBe = world.getBlockEntity(checkPos);

            if (checkBe instanceof PNDiodeEntity checkDiode) {
                // PNDiode가 있고, FACING이 자신과 같으면 한 칸 더 이동
                if (checkState.get(FACING) == facing) {
                    checkPos = checkPos.offset(checkDir);
                    continue; // 반복
                } else {
                    // FACING이 다르면 여기서 감지 종료
                    newPower = 0;
                    break;
                }
            } else {
                // PNDiode가 아닌 다른 블록이면, 그 블록의 레드스톤 파워 감지
                newPower = world.getEmittedRedstonePower(checkPos, checkDir.getOpposite());
                break;
            }
        }

        // 신호가 바뀌었을 때만 업데이트
        if (newPower != diodeEntity.getRedstonePower()) {
            diodeEntity.setRedstonePower(newPower);
            world.updateNeighborsAlways(pos, this);
        }
    }


    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PNDiodeEntity diodeEntity) {
            Direction checkDir = switch (state.get(FACING)) {
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.SOUTH;
                case NORTH -> Direction.WEST;
                case EAST -> Direction.NORTH;
                default -> null;
            };
            // checkDir 방향으로만 신호 출력
            if (checkDir != null && direction == checkDir) {
                return diodeEntity.getRedstonePower();
            }
        }
        return 0;
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PNDiodeEntity diodeEntity) {
            Direction checkDir = switch (state.get(FACING)) {
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.SOUTH;
                case NORTH -> Direction.WEST;
                case EAST -> Direction.NORTH;
                default -> null;
            };
            // checkDir 방향으로만 강한 신호 출력
            if (checkDir != null && direction == checkDir) {
                return diodeEntity.getRedstonePower();
            }
        }
        return 0;
    }



    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> SHAPE_WEST;
            case SOUTH -> SHAPE_EAST;
            case WEST  -> SHAPE_NORTH;
            case EAST  -> SHAPE_SOUTH;
            default   -> SHAPE_NORTH;
        };
    }



}

