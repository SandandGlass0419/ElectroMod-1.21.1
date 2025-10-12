package net.devs.electromod.block.custom.magnetic;

import net.devs.electromod.block.custom.magnetic.MagneticForce.ForceCompound;
import net.devs.electromod.block.custom.magnetic.MagneticForce.MagneticForceBlock;
import net.devs.electromod.block.entity.custom.magnetic.CoilBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class CoilBlock extends MagneticForceBlock
{
    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty DENSITY = IntProperty.of("density", 1, 3); // coil n
    public static final BooleanProperty POWERED = Properties.POWERED;

    private static final VoxelShape TOP = Block.createCuboidShape(0, 14, 0, 16, 16, 16);
    private static final VoxelShape BOTTOM = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    private static final VoxelShape NORTH = Block.createCuboidShape(0, 0, 0, 16, 16, 2);
    private static final VoxelShape SOUTH = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
    private static final VoxelShape EAST = Block.createCuboidShape(14, 0, 0, 16, 16, 16);
    private static final VoxelShape WEST = Block.createCuboidShape(0, 0, 0, 2, 16, 16);

    public static final VoxelShape X_SHAPE = VoxelShapes.union(TOP, BOTTOM, NORTH, SOUTH);
    public static final VoxelShape Y_SHAPE = VoxelShapes.union(NORTH, SOUTH, EAST, WEST);
    public static final VoxelShape Z_SHAPE = VoxelShapes.union(TOP, BOTTOM, EAST, WEST);

    public CoilBlock(Settings settings)
    {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP)
                .with(DENSITY, 2)
                .with(POWERED, false));
    }

    // register properties
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DENSITY, POWERED);
    }

    // settings for blockentity
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoilBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL; // uses block model for entity
    }

    // voxel methods
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return switch (state.get(FACING))
        {
            case UP, DOWN -> Y_SHAPE;
            case NORTH, SOUTH -> Z_SHAPE;
            case EAST, WEST -> X_SHAPE;
        };
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) { return VoxelShapes.fullCube(); }

    // blockstates - direction (facing)
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    // blockstates

    // used to have custom neighbor updater

    // redstone
    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        updatePower(world, pos, state);

        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        if (!world.isClient())
        {
            updatePower(world, pos, state);
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private void updatePower(World world, BlockPos pos, BlockState state)
    {
        if (!(world.getBlockEntity(pos) instanceof CoilBlockEntity coilBlockEntity)) return;

        int newPower = getRecievedRedstonePower(world, pos, state);

        if (newPower == 0 && state.get(POWERED))       { world.setBlockState(pos, state.with(POWERED, false), Block.NOTIFY_ALL); }
        else if (newPower != 0 && !state.get(POWERED)) { world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL); }

        coilBlockEntity.setRedstoneInput(newPower);
    }

    public static int getRecievedRedstonePower(World world, BlockPos pos, BlockState state)
    {
        Direction.Axis placedAxis = state.get(FACING).getAxis();

        return switch(placedAxis)
        {
            case Direction.Axis.X
                    -> getMaxPowerfromDirection(world, pos, Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);

            case Direction.Axis.Y
                    -> getMaxPowerfromDirection(world, pos, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

            case Direction.Axis.Z
                    -> getMaxPowerfromDirection(world, pos, Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
        };
    }

    private static int getMaxPowerfromDirection(World world, BlockPos pos, Direction... directions)
    {
        int max = 0;

        for (Direction dir : directions)
        {
            max = Math.max(world.getEmittedRedstonePower(pos.offset(dir), dir.getOpposite()), max);
            max = Math.max(world.getEmittedRedstonePower(pos.offset(dir), dir.getOpposite(), true), max);
        }

        return max;
    }

    // magnetic force features

    @Override
    public ForceCompound getForceCompound(World world, BlockPos pos)
    {
        if (!(world.getBlockEntity(pos) instanceof CoilBlockEntity coilBE)) return null;
        BlockState myState = world.getBlockState(pos);

        int magneticPower = defaultForceFormula(coilBE.getRedstoneInput(), myState.get(DENSITY));
        Direction direction = myState.get(FACING);

        return new ForceCompound(magneticPower, getForceProfile(world, pos, direction, magneticPower));
    }

    public int defaultForceFormula(int redstonePower, int density) { return 0; }
}