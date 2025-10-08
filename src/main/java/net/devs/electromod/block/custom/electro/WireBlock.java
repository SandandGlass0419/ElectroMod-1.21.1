package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WireBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    private static final VoxelShape CORE = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 5, 5, 5, 11, 11);

    public static final MapCodec<WireBlock> CODEC = WireBlock.createCodec(WireBlock::new);
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

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
       float absElectrocity = 0f;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof WireBlockEntity wireBlock) {
             absElectrocity = Math.abs(wireBlock.getStoredValue());
        }


        if (world.isClient()) return ActionResult.FAIL;
        Item stackItem = player.getMainHandStack().getItem();
        Item leftHandStackITem = player.getOffHandStack().getItem();

        if (stackItem instanceof DebugStickItem ||
                stackItem.equals(ModBlocks.COPPER_WIRE.asItem())||absElectrocity <=1f
                ||stackItem.equals(ModBlocks.WIRE.asItem())
                ||stackItem.equals(ModBlocks.GOLDEN_WIRE.asItem()))
            return ActionResult.FAIL;

        if (!stackItem.equals(ModItems.RUBBER_GLOVES) && !leftHandStackITem.equals(ModItems.RUBBER_GLOVES)) {
            BlockPos blockpos = player.getBlockPos();

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0, 0);

            world.spawnEntity(lightning);
            player.kill();
            player.sendMessage(Text.literal("Oops!"));
        }

        return ActionResult.PASS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;

        float absElectrocity = 0f;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof WireBlockEntity wireBlock) {
            absElectrocity = Math.abs(wireBlock.getStoredValue());
        }
        if (absElectrocity <=1f) return;
        BlockPos blockpos = entity.getBlockPos();

        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0, 0);

        world.spawnEntity(lightning);
        entity.kill();
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
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient()) {
            // 한 틱 뒤에 연결 계산 예약
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        super.scheduledTick(state, world, pos, random);
        checkAndChange(world, pos, true);
    }



    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) return;
        checkAndChange(world, pos, false);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void checkAndChange(World world, BlockPos pos, boolean value) {
        BlockState eastState = world.getBlockState(pos.offset(Direction.EAST));
        BlockState westState = world.getBlockState(pos.offset(Direction.WEST));
        BlockState southState = world.getBlockState(pos.offset(Direction.SOUTH));
        BlockState northState = world.getBlockState(pos.offset(Direction.NORTH));
        BlockState selfstate = world.getBlockState(pos);


        eastState = checkDirection(eastState, value, WireBlock.SOUTH, WireBlock.NORTH, WireBlock.EAST, WireBlock.WEST);
        westState = checkDirection(westState, value, WireBlock.NORTH, WireBlock.SOUTH, WireBlock.WEST, WireBlock.EAST);
        southState = checkDirection(southState, value, WireBlock.WEST, WireBlock.EAST, WireBlock.SOUTH, WireBlock.NORTH);
        northState = checkDirection(northState, value, WireBlock.EAST, WireBlock.WEST, WireBlock.NORTH, WireBlock.SOUTH);
        selfstate =checkDirection(selfstate, value,WireBlock.SOUTH,WireBlock.SOUTH,WireBlock.SOUTH,WireBlock.SOUTH); //설치한블록 방향설정

        world.setBlockState(pos, selfstate, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.EAST), eastState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.WEST), westState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.SOUTH), southState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.NORTH), northState, Block.NOTIFY_ALL);
    }

    private BlockState checkDirection(BlockState state, boolean value, BooleanProperty... hasDirection) {
        if (!(state.getBlock() instanceof WireBlock)) return state;

        Direction facing = state.get(FACING);
        return switch (facing) {
            case EAST -> state.with(hasDirection[0], value);
            case WEST -> state.with(hasDirection[1], value);
            case SOUTH -> state.with(hasDirection[2], value);
            case NORTH -> state.with(hasDirection[3], value);
            default -> state;
        };
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = CORE;

        Direction facing = state.get(FACING);
        Direction back = facing.getOpposite();
        Direction left = facing.getAxis().isHorizontal() ? facing.rotateYCounterclockwise() : null;
        Direction right = facing.getAxis().isHorizontal() ? facing.rotateYClockwise() : null;

        if (state.get(NORTH)) shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(facing));
        if (state.get(SOUTH)) shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(back));
        if (state.get(EAST)  && right != null) shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(right));
        if (state.get(WEST)  && left != null)  shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(left));

        return shape;
    }

    private static VoxelShape getShapeForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST  -> EAST_SHAPE;
            case WEST  -> WEST_SHAPE;
            default -> CORE;
        };
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClient() && type == ModBlockEntities.WIRE_BE)
            return (w, pos, s, blockEntity) -> {
                if (!(blockEntity instanceof WireBlockEntity wireBE)) return;

                Direction facing = s.get(FACING);
                BlockPos targetPos = pos.offset(facing);
                BlockState targetState = w.getBlockState(targetPos);

                if (targetState.getBlock() instanceof WireBlock) {
                    BlockEntity targetBE = w.getBlockEntity(targetPos);
                    if (targetBE instanceof WireBlockEntity targetWireBE) {
                        float value = wireBE.getStoredValue();
                        targetWireBE.setStoredValue(value/resistance);
                    }
                }
            };
        return null;
    }
}
