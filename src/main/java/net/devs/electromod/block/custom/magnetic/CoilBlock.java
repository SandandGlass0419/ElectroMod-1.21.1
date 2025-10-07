package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.block.entity.custom.magnetic.CoilBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
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

public class CoilBlock extends BlockWithEntity implements BlockEntityProvider
{
    public static final MapCodec<? extends BlockWithEntity> CODEC = CoilBlock.createCodec(CoilBlock::new);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty DENSITY = IntProperty.of("density", 2, 4); // coil n

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
                .with(DENSITY, 3));
    }

    // register properties
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DENSITY);
    }

    // settings for blockentity
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoilBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL; // uses block model for entity
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return validateTicker(type, ModBlockEntities.COIL_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
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
            default -> Y_SHAPE;
        };
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos)
    {
        return VoxelShapes.fullCube();
    }

    // blockstate - direction (facing)
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    // redstone
    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof CoilBlockEntity coilBlockEntity) {
            coilBlockEntity.setRedstoneInput(getRecievedRedstonePower(world, pos, state));
        }

        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        if (world.isClient()) super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        if (world.getBlockEntity(pos) instanceof CoilBlockEntity coilBlockEntity)
        {
            int power = getRecievedRedstonePower(world, pos, state);
            coilBlockEntity.setRedstoneInput(power);
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
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

        for (Direction dir : directions) {
            max = Math.max(world.getEmittedRedstonePower(pos.offset(dir), dir.getOpposite()), max);
        }

        return max;
    }
}