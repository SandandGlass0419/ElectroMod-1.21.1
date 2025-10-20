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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ElectroDector extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    // VoxelShape 정의 (JSON 모델 기준)
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape UP_SHAPE    = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape DOWN_SHAPE  = Block.createCuboidShape(0, 0, 0, 16, 8, 16);

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
        return switch (state.get(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST  -> EAST_SHAPE;
            case WEST  -> WEST_SHAPE;
            case UP    -> UP_SHAPE;
            case DOWN  -> DOWN_SHAPE;
        };
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            double maxElectrocity = Double.NEGATIVE_INFINITY;
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();

            int range = 1; // 탐색 범위 반경 1블록

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
        Direction[] horizontals = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        // 최대 전류 탐색
        for (Direction dir : horizontals) {
            BlockPos currentPos = pos.offset(dir);

            while (true) {
                BlockEntity be = world.getBlockEntity(currentPos);
                Block block = world.getBlockState(currentPos).getBlock();

                if (be instanceof WireBlockEntity wire) {
                    float e = wire.getElectrocity();
                    if (!found || e > maxElectrocity) {
                        maxElectrocity = e;
                        found = true;
                    }
                    break; // Wire 만나면 종료
                } else if (block instanceof PNDiode || block instanceof ElectroDector) {
                    currentPos = currentPos.offset(dir); // 같은 방향으로 계속 이동
                } else {
                    break; // Wire, PNDiode, ElectroDector 없으면 종료
                }
            }
        }

        if (found) {
            // 최대 전류 적용
            for (Direction dir : horizontals) {
                BlockPos currentPos = pos.offset(dir);

                while (true) {
                    BlockEntity be = world.getBlockEntity(currentPos);
                    Block block = world.getBlockState(currentPos).getBlock();

                    if (be instanceof WireBlockEntity wire) {
                        BlockState wireState = world.getBlockState(currentPos);

                        // ELECTRIFIED 체크
                        if (!wireState.get(WireBlock.ELECTRIFIED)) {
                            wire.setElectrocity(maxElectrocity, world, currentPos, wireState, wire);
                        }
                        break;
                    } else if (block instanceof PNDiode || block instanceof ElectroDector) {
                        currentPos = currentPos.offset(dir); // 같은 방향으로 계속 이동
                    } else {
                        break;
                    }
                }
            }
        }
    }




    }
