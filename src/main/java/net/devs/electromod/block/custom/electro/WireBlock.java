package net.devs.electromod.block.custom.electro;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.devs.electromod.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class WireBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    protected static final VoxelShape CORE = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
    protected static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
    protected static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 5, 5, 5, 11, 11);

    public float resistance = 1.0f;

    public WireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false));
    }

    // ⚡ 우클릭 감전 이벤트 (모든 WireBlock 기본)
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, net.minecraft.util.hit.BlockHitResult hit) {
        if (world.isClient()) return ActionResult.FAIL;

        Item mainHand = player.getMainHandStack().getItem();
        Item offHand = player.getOffHandStack().getItem();

        // DebugStick이나 같은 WireBlock 아이템이면 무시
        if (mainHand instanceof DebugStickItem ||
                mainHand.equals(ModBlocks.COPPER_WIRE.asItem())) return ActionResult.FAIL;

        // 장갑이 없으면 감전
        if (!mainHand.equals(ModItems.RUBBER_GLOVES) && !offHand.equals(ModItems.RUBBER_GLOVES)) {
            spawnLightningAtEntity(world, player);
            player.kill();
            player.sendMessage(Text.literal("Oops!"));
        }

        return ActionResult.PASS;
    }

    // ⚡ 밟았을 때 감전 이벤트
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;
        spawnLightningAtEntity(world, entity);
        entity.kill();
    }

    protected void spawnLightningAtEntity(World world, Entity entity) {
        BlockPos pos = entity.getBlockPos();
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
        world.spawnEntity(lightning);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        checkAndChange(world, pos, true);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) return;
        checkAndChange(world, pos, false);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    protected void checkAndChange(World world, BlockPos pos, boolean value) {
        for (Direction dir : Direction.values()) {
            if (dir.getAxis().isHorizontal()) {
                BlockPos offsetPos = pos.offset(dir);
                BlockState neighborState = world.getBlockState(offsetPos);
                world.setBlockState(offsetPos, checkDirection(neighborState, value, dir), Block.NOTIFY_ALL);
            }
        }
    }

    protected BlockState checkDirection(BlockState state, boolean value, Direction dir) {
        if (!(state.getBlock() instanceof WireBlock)) return state;

        Direction facing = state.get(FACING);
        BooleanProperty[] props = switch (facing) {
            case EAST -> new BooleanProperty[]{SOUTH, NORTH, EAST, WEST};
            case WEST -> new BooleanProperty[]{NORTH, SOUTH, WEST, EAST};
            case SOUTH -> new BooleanProperty[]{WEST, EAST, SOUTH, NORTH};
            case NORTH -> new BooleanProperty[]{EAST, WEST, NORTH, SOUTH};
            default -> new BooleanProperty[]{};
        };

        return switch (dir) {
            case EAST -> state.with(props[0], value);
            case WEST -> state.with(props[1], value);
            case SOUTH -> state.with(props[2], value);
            case NORTH -> state.with(props[3], value);
            default -> state;
        };
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        VoxelShape shape = CORE;
        if (state.get(NORTH)) shape = net.minecraft.util.shape.VoxelShapes.union(shape, NORTH_SHAPE);
        if (state.get(SOUTH)) shape = net.minecraft.util.shape.VoxelShapes.union(shape, SOUTH_SHAPE);
        if (state.get(EAST))  shape = net.minecraft.util.shape.VoxelShapes.union(shape, EAST_SHAPE);
        if (state.get(WEST))  shape = net.minecraft.util.shape.VoxelShapes.union(shape, WEST_SHAPE);
        return shape;
    }

    @Override
    public abstract @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Override
    protected abstract BlockRenderType getRenderType(BlockState state);

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient() && type == ModBlockEntities.WIRE_BE)
            return (w, pos, s, blockEntity) -> {
                if (!(blockEntity instanceof WireBlockEntity wireBE)) return;

                // FACING 방향 블록 위치
                Direction facing = s.get(FACING);
                BlockPos targetPos = pos.offset(facing);
                BlockState targetState = w.getBlockState(targetPos);

                // FACING 방향 블록이 CopperWire이면
                if (targetState.getBlock() instanceof CopperWire) {
                    BlockEntity targetBE = w.getBlockEntity(targetPos);
                    if (targetBE instanceof WireBlockEntity targetWireBE) {
                        // 현재 WireBlockEntity의 값 가져와서 전달
                        float value = wireBE.getStoredValue();
                        targetWireBE.setStoredValue(value/resistance);
                    }
                }
            };
        return null;
    }

    public void setResistance(float value)
    {
        resistance = value;
    }

}
