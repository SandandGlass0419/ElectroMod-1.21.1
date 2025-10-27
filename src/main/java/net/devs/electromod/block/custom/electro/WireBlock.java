package net.devs.electromod.block.custom.electro;

import com.google.common.base.Suppliers;
import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.devs.electromod.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
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

import java.util.Set;
import java.util.function.Supplier;

public abstract class WireBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty ELECTRIFIED = BooleanProperty.of("electrified"); // 새로 추가

    private static final VoxelShape CORE = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
    private static final VoxelShape EAST_SHAPE  = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
    private static final VoxelShape WEST_SHAPE  = Block.createCuboidShape(0, 5, 5, 5, 11, 11);

    public static final float MAX_POWER = 180f;
    public static final float MIN_DEATH_POWER = 10f;

    public WireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(ELECTRIFIED, false)); // 기본값 false
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        float absElectrocity = 0f;

        if (world.getBlockEntity(pos) instanceof WireBlockEntity wireBE) {
            absElectrocity = Math.abs(wireBE.getElectricity());
        }

        if (world.isClient()) return ActionResult.FAIL;

        Item stackItem = player.getMainHandStack().getItem();
        Item leftHandStackItem = player.getOffHandStack().getItem();

        if (stackItem instanceof DebugStickItem
            || stackItem.equals(ModBlocks.COPPER_WIRE.asItem())
            || stackItem.equals(ModBlocks.WIRE.asItem())
            || stackItem.equals(ModBlocks.GOLDEN_WIRE.asItem())
            || absElectrocity <= MIN_DEATH_POWER)
            return ActionResult.FAIL;

        if (!stackItem.equals(ModItems.RUBBER_GLOVES) && !leftHandStackItem.equals(ModItems.RUBBER_GLOVES)) {
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

        if (world.getBlockEntity(pos) instanceof WireBlockEntity wireBE &&
                Math.abs(wireBE.getElectricity()) <= MIN_DEATH_POWER) return;

        if (entity instanceof LivingEntity living &&
                living.getEquippedStack(EquipmentSlot.FEET).isOf(Items.LEATHER_BOOTS)) return;

        BlockPos blockpos = entity.getBlockPos();
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0, 0);

        world.spawnEntity(lightning);
        entity.kill();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, NORTH, EAST, SOUTH, WEST, ELECTRIFIED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    public static final Supplier<Set<Block>> interactableTargets = Suppliers.memoize(() -> Set.of(
            ModBlocks.ACDC_CONVERTER,
            ModBlocks.ELECTRO_DECTOR
    ));

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (world.isClient()) return;

        boolean northHasWire = false;
        boolean southHasWire = false;
        boolean eastHasWire = false;
        boolean westHasWire = false;

        for (Direction dir : Direction.values()) {
            if (dir.getAxis() == Direction.Axis.Y) continue;

            Block targetBlock = world.getBlockState(pos.offset(dir)).getBlock();

            if (targetBlock instanceof WireBlock || interactableTargets.get().contains(targetBlock))
            {
                switch (dir) {
                    case NORTH -> northHasWire = true;
                    case SOUTH -> southHasWire = true;
                    case EAST  -> eastHasWire = true;
                    case WEST  -> westHasWire = true;
                }
            }
        }

        switch (state.get(FACING)) {
            case NORTH -> state = setState(state, eastHasWire, westHasWire, northHasWire, southHasWire);
            case SOUTH -> state = setState(state, westHasWire, eastHasWire, southHasWire, northHasWire);
            case EAST  -> state = setState(state, southHasWire, northHasWire, eastHasWire, westHasWire);
            case WEST  -> state = setState(state, northHasWire, southHasWire, westHasWire, eastHasWire);
            case UP    -> state = setState(state, eastHasWire, westHasWire, northHasWire, southHasWire);
            case DOWN  -> state = setState(state, westHasWire, eastHasWire, southHasWire, northHasWire);
        }

        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }

    private BlockState setState(BlockState state, boolean... values) {
        return state.with(EAST, values[0]).with(WEST, values[1]).with(NORTH, values[2]).with(SOUTH, values[3]);
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
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public abstract float getElectricResistance();

    public void onBlockElectricityUpdated(World w, BlockPos pos, WireBlockEntity wireBE) {
        BlockState state = w.getBlockState(pos);
        if (!(state.getBlock() instanceof WireBlock)) return;

        float value = wireBE.getElectricity() / getElectricResistance();
        wireBE.setElectricity(value);

        for (Direction dir : Direction.values()) {
            // 위/아래 방향일 때 FACING 체크
            if ((dir == Direction.UP || dir == Direction.DOWN) &&
                    (state.get(FACING) != Direction.UP && state.get(FACING) != Direction.DOWN)
            ) continue; // 위/아래 전도 불가, 스킵

            BlockPos targetPos = pos.offset(dir);
            BlockState targetState = w.getBlockState(targetPos);

            // ElectroDector라면 연속으로 있는 동안 계속 한 칸씩 더 감
            while (targetState.getBlock() instanceof ElectroDector) {
                targetPos = targetPos.offset(dir);
                targetState = w.getBlockState(targetPos);
            }

            if (!(w.getBlockEntity(targetPos) instanceof WireBlockEntity targetWireBE)) continue;

            targetWireBE.updateElectricity(value);
        }
    }


    public boolean IsElectrified(BlockState state) {
        return state.get(ELECTRIFIED);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.WIRE_BE) return null;

        return (w, pos, s, blockEntity) -> {
                WireBlockEntity wireBE = (WireBlockEntity) blockEntity;

                wireBE.incrementTickCounter(1);
                if (wireBE.getTickCounter() >= 20) {
                    wireBE.setTickCounter(0);

                    // 월드에 상태 적용
                    w.setBlockState(pos, s.with(ELECTRIFIED, false), Block.NOTIFY_ALL);
                }
            };
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (world.isClient()) return; // 클라 쪽에서는 무시

        BlockEntity selfBE = world.getBlockEntity(pos);
        if (!(selfBE instanceof WireBlockEntity selfWireBE)) return;

        float minElectrocity = MAX_POWER; // 최소 전기값 저장용
        boolean foundNeighbor = false;

        for (Direction dir : Direction.values())
        {
            if (!(world.getBlockEntity(pos.offset(dir)) instanceof WireBlockEntity neighborWireBE)) continue;

            float neighborValue = neighborWireBE.getElectricity();
            if (neighborValue < minElectrocity) {
                    minElectrocity = neighborValue;
                    foundNeighbor = true;
            }
        }

        // 인접 와이어가 하나라도 있었을 경우
        if (foundNeighbor) {
            selfWireBE.updateElectricity(selfWireBE.getElectricity() + minElectrocity);
        }
    }
}
