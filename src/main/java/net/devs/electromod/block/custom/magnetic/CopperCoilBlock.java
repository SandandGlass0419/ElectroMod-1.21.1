package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.components.ModDataComponentTypes;
import net.devs.electromod.item.custom.magnetic.MagnetItem;
import net.devs.electromod.item.entity.custom.magnetic.CoilBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperCoilBlock extends BlockWithEntity implements BlockEntityProvider
{
    public static final MapCodec<CopperCoilBlock> CODEC = CopperCoilBlock.createCodec(CopperCoilBlock::new);
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

    public CopperCoilBlock(Settings settings)
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
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

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
            default -> Y_SHAPE;
        };
    }

    // blockstate - direction (facing)
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    // block features
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit)
    {
        if (!(world.getBlockEntity(pos) instanceof CoilBlockEntity)) return ActionResult.FAIL;
        if (!player.getAbilities().allowModifyWorld) return ActionResult.PASS;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof MagnetItem) // custom action
        {
            int magnet_force = stack.getOrDefault(ModDataComponentTypes.MAGNET_FORCE, ModDataComponentTypes.min_force);
            player.sendMessage(Text.literal("Current force: " + magnet_force), true);

            return ActionResult.SUCCESS;
        }

        else // default action
        {
            BlockState newBlockState = state.cycle(DENSITY);
            int newDensity = newBlockState.get(DENSITY);

            world.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_GRATE_HIT, SoundCategory.BLOCKS);

            return ActionResult.success(world.isClient);
        }
    }
}