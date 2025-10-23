package net.devs.electromod.block.custom.magnetic;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.custom.magnetic.MagneticForce.*;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.block.entity.custom.magnetic.MagneticDetectorEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class  MagneticDetector extends AbstractDetectorBlock
{
    public static final MapCodec<MagneticDetector> CODEC = MagneticDetector.createCodec(MagneticDetector::new);
    public static final EnumProperty<DetectState> DETECT_STATE = EnumProperty.of("detect_state", DetectState.class);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public static final VoxelShape UP_Z_SHAPE = Block.createCuboidShape(0, 0, 6, 16, 15, 10);
    public static final VoxelShape UP_X_SHAPE = Block.createCuboidShape(6, 0, 0, 10, 15, 16);
    public static final VoxelShape DOWN_Z_SHAPE = Block.createCuboidShape(0, 1, 6, 16, 16, 10);
    public static final VoxelShape DOWN_X_SHAPE = Block.createCuboidShape(6, 1, 0, 10, 16, 16);
    public static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 6, 1, 16, 10, 16); // X/Z same
    public static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 6, 0, 16, 10, 15); // X/Z same
    public static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0, 6, 0, 15, 10, 16); // X/Z same
    public static final VoxelShape WEST_SHAPE = Block.createCuboidShape(1, 6, 0, 16, 10, 16); // X/Z same

    public MagneticDetector(Settings settings)
    {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(DETECT_STATE, DetectState.EMPTY)
                .with(FACING, Direction.UP)
                .with(HORIZONTAL_AXIS, Direction.Axis.Z)
                .with(POWERED, false));
    }

    // register properties
    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DETECT_STATE, FACING, HORIZONTAL_AXIS, POWERED);
    }

    // block entity methods
    @Override protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Nullable
    @Override public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MagneticDetectorEntity(pos, state);
    }

    @Override protected BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    // voxel methods
    @Override protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        Direction facing = state.get(FACING);
        Direction.Axis axis = state.get(HORIZONTAL_AXIS);

        switch (facing)
        {
            case UP: switch (axis) {
                case Z: return UP_Z_SHAPE;
                case X: return UP_X_SHAPE;
            }

            case DOWN: switch (axis) {
                case Z: return DOWN_Z_SHAPE;
                case X: return DOWN_X_SHAPE;
            }

            case NORTH: return NORTH_SHAPE;
            case SOUTH: return SOUTH_SHAPE;
            case EAST: return EAST_SHAPE;
            case WEST: return WEST_SHAPE;

            default: return UP_Z_SHAPE;
        }
    }

    // blockstates - direction (facing, horizontal axis)
    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState()
                .with(FACING, ctx.getSide())
                .with(HORIZONTAL_AXIS, ctx.getHorizontalPlayerFacing().getAxis());
    }

    public static Direction.Axis getDetectionAxis(BlockState state)
    {
        if (state.get(FACING).getAxis() != Direction.Axis.Y) return Direction.Axis.Y;

        return state.get(HORIZONTAL_AXIS);
    }

    // blockstates
    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (world.isClient()) return;
        if (state.getBlock() == oldState.getBlock()) return;

        ElectroMod.LOGGER.info("onBlockAdded ran updatePower");
        updatePower(world, pos, state, 0);
        this.updateNeighbors(world, pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!(world.getBlockEntity(pos) instanceof MagneticDetectorEntity)) return;
        if (world.isClient()) return;

        if (!(newState.getBlock() instanceof MagneticDetector)) // block broken
        {
            ItemStack stack = state.get(DETECT_STATE).getStoredItem();
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }

        this.updateNeighbors(world, pos, state);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        Direction facing = state.get(FACING);
        BlockPos supportPos = pos.offset(facing.getOpposite());
        return world.getBlockState(supportPos).isSideSolidFullSquare(world, supportPos, facing);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        return direction == state.get(FACING).getOpposite() && !canPlaceAt(state, world, pos) ?
                Blocks.AIR.getDefaultState() :
                super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private void updateNeighbors(World world, BlockPos pos, BlockState state)
    {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
    }

    // redstone
    @Override public boolean emitsRedstonePower(BlockState state) { return true; }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        if (!(world.getBlockEntity(pos) instanceof MagneticDetectorEntity detectorEntity)) return 0;

        return getRedstoneDirections(state, false).contains(direction) && state.get(POWERED) ?
                detectorEntity.getRedstoneOutput() : 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
    {
        if (!(world.getBlockEntity(pos) instanceof MagneticDetectorEntity detectorEntity)) return 0;

        return getRedstoneDirections(state, true).contains(direction) && state.get(POWERED) ?
                detectorEntity.getRedstoneOutput() : 0;
    }

    public static Set<Direction> getRedstoneDirections(BlockState state, boolean isstrong)
    {
        Direction facing = state.get(FACING);
        if (isstrong || facing.getAxis() != Direction.Axis.Y) return Set.of(facing);

        return switch (state.get(HORIZONTAL_AXIS))
        {
            case X -> Set.of(Direction.NORTH, Direction.SOUTH);
            case Z -> Set.of(Direction.EAST, Direction.WEST);
            default -> Set.of();
        };
    }

    private void updatePower(World world, BlockPos pos, BlockState state, int magenticPower)
    {
        if (!(world.getBlockEntity(pos) instanceof MagneticDetectorEntity detectorEntity)) return;

        int newPower = defaultPowerConverter(state, magenticPower);
        ElectroMod.LOGGER.info("newPower: {}", newPower);

        if (newPower == 0 && state.get(POWERED))       { world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL); }
        else if (newPower != 0 && !state.get(POWERED)) { world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL); }

        this.updateNeighbors(world, pos, state);
        detectorEntity.setRedstoneOutput(newPower);
    }

    public static int defaultPowerConverter(BlockState state, int magneticPower)
    {
        if (state.get(DETECT_STATE) == DetectState.EMPTY)
        {
            // code for generic
        }

        return Math.min(magneticPower / CoilBlock.DENSITY_MAX, 15);
    }

    // magnetic features
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return validateTicker(type, ModBlockEntities.DETECTOR_BE, this::tick);
    }

    @Override
    public void watchIntick(World world1, BlockPos pos, BlockState state1, AbstractDetectorBlockEntity abstractBE)
    {
        if (world1.isClient()) return;
        if (!(abstractBE instanceof MagneticDetectorEntity detectorBE)) return;

        BlockField blockField = MagneticForceInteractor.getBlockField(world1, pos); if (blockField == null) return;
        Direction.Axis detectionAxis = getDetectionAxis(state1);
        ForceProfile.powerCategory powerCategory = getPowerCategory(state1.get(DETECT_STATE));
        Integer normalizer = MagneticForceInteractor.getAdditiveFactor(powerCategory); if (normalizer == null) return;

        Set<BlockPos> excludedPos = new HashSet<>();

        excludeFilled(excludedPos, world1, detectorBE);
        excludeUnaligned(excludedPos, blockField, detectionAxis);
        excludeWrongCategory(excludedPos, world1, blockField, powerCategory);

        int power = BlockField.normalize(blockField.getPureAdditive(excludedPos), normalizer);
        ElectroMod.LOGGER.info("magneticPower: {}", power);

        updatePower(world1, pos, state1, power);
    }

    public static void excludeFilled(Set<BlockPos> excludedPos, World world, MagneticDetectorEntity detectorBE)
    {
        for (var magneticPos : detectorBE.getWatch().keySet())
        {
            if (excludedPos.contains(magneticPos)) continue;

            for (var fieldPos : detectorBE.getWatch().get(magneticPos))
            {
                if (isFilled(world.getBlockState(fieldPos)))
                {
                    excludedPos.add(magneticPos);
                    ElectroMod.LOGGER.info("excluded: {}, {}", magneticPos, world.getBlockState(fieldPos).toString());
                }
            }
        }
    }

    public static boolean isFilled(BlockState state)
    {
        if (state.emitsRedstonePower()) return false;

        if (state.isIn(BlockTags.AIR) ||
            state.isIn(BlockTags.FIRE)) return false;

        if (notFilled.get().contains(state.getBlock())) return false;

        return true;
    }

    public static final Supplier<Set<Block>> notFilled = Suppliers.memoize(() -> Set.of(
            Blocks.LIGHT,
            Blocks.LAVA,
            Blocks.WATER,
            ModBlocks.IRON_COIL,
            ModBlocks.GOLDEN_COIL,
            ModBlocks.COPPER_COIL,
            ModBlocks.MAGNET_BLOCK
    ));

    public static void excludeUnaligned(Set<BlockPos> excludedPos, BlockField blockField, Direction.Axis detectionAxis)
    {
        for (var magneticPos : blockField.getFields().keySet())
        {
            if (excludedPos.contains(magneticPos)) continue;

            var debug = blockField.get(magneticPos).getForceDirection().getAxis();
            if (debug != detectionAxis)
            { ElectroMod.LOGGER.info("axis: {}, {}", debug.asString(), detectionAxis.asString()); excludedPos.add(magneticPos); }
        }
    }

    public static void excludeWrongCategory(Set<BlockPos> excludedPos, World world, BlockField blockField, ForceProfile.powerCategory stateCat)
    {
        int magneticBlockPower;

        for (var magneticPos : blockField.getFields().keySet())
        {
            if (excludedPos.contains(magneticPos)) continue;

            magneticBlockPower = MagneticForceInteractor.getField(world, magneticPos).getMagneticPower();

            if (MagneticForceInteractor.getPowerCategory(magneticBlockPower) != stateCat)
            { ElectroMod.LOGGER.info("cat: {}, {}", MagneticForceInteractor.getPowerCategory(magneticBlockPower), stateCat); excludedPos.add(magneticPos); }
        }
    }

    private static ForceProfile.powerCategory getPowerCategory(DetectState detectState)
    {
        return switch (detectState)
        {
            case IRON_COIL -> ForceProfile.powerCategory.IRON;
            case GOLDEN_COIL -> ForceProfile.powerCategory.GOLD;
            case COPPER_COIL -> ForceProfile.powerCategory.COPPER;
            case EMPTY -> ForceProfile.powerCategory.GENERIC;
        };
    }

    // block features
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
    {
        if (!(world.getBlockEntity(pos) instanceof MagneticDetectorEntity)) return ActionResult.FAIL;
        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;
        if (player.isSneaking()) return ActionResult.FAIL;

        ItemStack stack = player.getMainHandStack();
        DetectState detect_state = state.get(DETECT_STATE);

        if (stack.getItem() instanceof DebugStickItem)
        {
            if (world.isClient()) return ActionResult.FAIL;

            magneticTest(world, pos, player);
            return ActionResult.SUCCESS;
        }

        if (detect_state != DetectState.EMPTY) // give item to player
        {
            player.giveItemStack(detect_state.getStoredItem());
            world.setBlockState(pos, state.with(DETECT_STATE, DetectState.EMPTY), Block.NOTIFY_ALL);

            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_STEP, SoundCategory.BLOCKS);
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, 1f);

            return ActionResult.SUCCESS;
        }

        DetectState foundState = DetectState.findStateWith(stack.getItem());

        if (foundState != null) // get item from player
        {
            world.setBlockState(pos, state.with(DETECT_STATE, foundState), Block.NOTIFY_ALL);
            if (!player.isCreative()) { stack.decrement(1); }

            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_STEP, SoundCategory.BLOCKS);

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private void magneticTest(World world, BlockPos pos, PlayerEntity player)
    {
        player.sendMessage(Text.literal(MagneticForceInteractor.testPlacementCheck(world, pos).toString()), true);
    }
}