package net.devs.electromod.block.custom.electro;


import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.ModBlocks;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperWire extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    private static final VoxelShape CORE = Block.createCuboidShape(5, 5, 5, 11, 11, 11); // 중심 점(dot)

    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);  // 북쪽 연결
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 11, 11, 11, 16); // 남쪽 연결
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(11, 5, 5, 16, 11, 11); // 동쪽 연결
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 5, 5, 5, 11, 11);   // 서쪽 연결

    public static final MapCodec<CopperWire> CODEC = CopperWire.createCodec(CopperWire::new);

    public CopperWire(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false));
    }



    // ⚡ 우클릭 감전 이벤트
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.FAIL;
        Item stackItem = player.getMainHandStack().getItem();
        Item leftHandStackITem = player.getOffHandStack().getItem();

        if (stackItem instanceof DebugStickItem ||
                stackItem.equals(ModBlocks.COPPER_WIRE.asItem())) return ActionResult.FAIL;

        if (!stackItem.equals(ModItems.RUBBER_GLOVES)&&!leftHandStackITem.equals(ModItems.RUBBER_GLOVES)) {
            BlockPos blockpos = player.getBlockPos();

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0, 0);

            world.spawnEntity(lightning);
            player.kill();
            player.sendMessage(Text.literal("Oops!"));
        }

        return ActionResult.PASS;
    }

    // ⚡ 밟았을 때 감전 이벤트
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient()) return;

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
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
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

        eastState = checkDirection(eastState, value, CopperWire.SOUTH, CopperWire.NORTH, CopperWire.EAST, CopperWire.WEST);
        westState = checkDirection(westState, value, CopperWire.NORTH, CopperWire.SOUTH, CopperWire.WEST, CopperWire.EAST);
        southState = checkDirection(southState, value, CopperWire.WEST, CopperWire.EAST, CopperWire.SOUTH, CopperWire.NORTH);
        northState = checkDirection(northState, value, CopperWire.EAST, CopperWire.WEST, CopperWire.NORTH, CopperWire.SOUTH);

        world.setBlockState(pos.offset(Direction.EAST), eastState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.WEST), westState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.SOUTH), southState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.NORTH), northState, Block.NOTIFY_ALL);
    }

    private BlockState checkDirection(BlockState state, boolean value, BooleanProperty... hasDirection) {
        if (!(state.getBlock() instanceof CopperWire)) return state;

        Direction facing = state.get(FACING);
        return switch (facing) {
            case Direction.EAST -> state.with(hasDirection[0], value);
            case Direction.WEST -> state.with(hasDirection[1], value);
            case Direction.SOUTH -> state.with(hasDirection[2], value);
            case Direction.NORTH -> state.with(hasDirection[3], value);
            default -> state;
        };
    }


    //Voxel


    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = CORE; // 중심은 항상 표시

        Direction facing = state.get(FACING);

        // 블록기준 상대 방향 계산
        Direction back = facing.getOpposite();
        Direction left = facing.getAxis().isHorizontal() ? facing.rotateYCounterclockwise() : null;
        Direction right = facing.getAxis().isHorizontal() ? facing.rotateYClockwise() : null;

        // 연결 여부에 따라 shape 합치기
        if (state.get(NORTH)) shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(facing));
        if (state.get(SOUTH)) shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(back));
        if (state.get(EAST)  && right != null) shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(right));
        if (state.get(WEST)  && left != null)  shape = net.minecraft.util.shape.VoxelShapes.union(shape, getShapeForDirection(left));

        return shape;
    }

    // 절대 좌표 방향에 맞는 shape 반환
    private static VoxelShape getShapeForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST  -> EAST_SHAPE;
            case WEST  -> WEST_SHAPE;
            default -> CORE; // UP/DOWN은 필요 없음
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


        return null;
    }
}
