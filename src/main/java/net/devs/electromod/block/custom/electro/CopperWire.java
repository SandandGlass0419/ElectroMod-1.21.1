package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CopperWire extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;

    public CopperWire(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false));
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return super.getCodec();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.FAIL;

        if (!player.getMainHandStack().getItem().equals(ModItems.RUBBER_GLOVES)) {
            BlockPos blockpos = player.getBlockPos();

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0, 0);

            world.spawnEntity(lightning);

            player.kill();
            player.sendMessage(Text.literal("Oops!"));
        }
        return ActionResult.SUCCESS;
    }

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
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClient()) return;

        // 단순화: 항상 월드의 north/east/south/west 검사
        BlockState newState = state
                .with(NORTH, world.getBlockState(pos.north()).isOf(this))
                .with(EAST,  world.getBlockState(pos.east()).isOf(this))
                .with(SOUTH, world.getBlockState(pos.south()).isOf(this))
                .with(WEST,  world.getBlockState(pos.west()).isOf(this));

        if (!newState.equals(state)) {
            world.setBlockState(pos, newState, 3);
            state = newState;
        }

        // 주변 동일 블록들의 해당(반대) 방향 프로퍼티를 true로 설정 (양방향 연결)
        for (Direction dir : Direction.values()) {
            if (!dir.getAxis().isHorizontal()) continue;

            BlockPos nPos = pos.offset(dir);
            BlockState nState = world.getBlockState(nPos);

            if (!nState.isOf(this)) continue;

            // neighbor가 우리를 바라보는 방향의 프로퍼티를 가져온다 (반대 방향)
            BooleanProperty neighborProp = getPropertyFor(dir.getOpposite());
            if (!nState.get(neighborProp)) {
                BlockState updated = nState.with(neighborProp, true);
                world.setBlockState(nPos, updated, 3);
            }
        }

    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // direction: world direction FROM this block TO neighbor
        if (direction.getAxis().isHorizontal()) {
            BooleanProperty prop = getPropertyFor(direction);
            boolean connect = world.getBlockState(neighborPos).isOf(this);
            return state.with(prop, connect);
        }
        return state;
    }

    // 방향 -> BooleanProperty 매핑 헬퍼
    private static BooleanProperty getPropertyFor(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST  -> EAST;
            case SOUTH -> SOUTH;
            case WEST  -> WEST;
            default    -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        };
    }
}


