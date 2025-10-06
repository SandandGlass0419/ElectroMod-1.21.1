package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.custom.magnetic.MagneticDetectorEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MagneticDetector extends BlockWithEntity
{
    public static final MapCodec<MagneticDetector> CODEC = MagneticDetector.createCodec(MagneticDetector::new);
    public static final IntProperty DETECT_MODE = IntProperty.of("detect_mode", 0, 3);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<Direction.Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;

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
                .with(DETECT_MODE, 0)
                .with(FACING, Direction.UP)
                .with(HORIZONTAL_AXIS, Direction.Axis.Z));
    }

    // register properties
    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DETECT_MODE, FACING, HORIZONTAL_AXIS);
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

    // block features
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
    {
        return ActionResult.SUCCESS;
    }
}
