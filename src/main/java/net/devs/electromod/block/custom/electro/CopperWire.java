package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.World;

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

    // ⚡ 우클릭 감전 이벤트
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.FAIL;
        Item stackItem = player.getMainHandStack().getItem();

        if (stackItem instanceof DebugStickItem ||
            stackItem.equals(ModBlocks.COPPER_WIRE.asItem())) return ActionResult.FAIL;

        if (!stackItem.equals(ModItems.RUBBER_GLOVES)) {
            BlockPos blockpos = player.getBlockPos();

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0, 0);

            world.spawnEntity(lightning);
            player.kill();
            player.sendMessage(Text.literal("Oops!"));
        }

        return ActionResult.SUCCESS;
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

    @Override protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() == newState.getBlock()) return;

        checkAndChange(world, pos, false);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void checkAndChange(World world, BlockPos pos, boolean value)
    {
        BlockState eastState = world.getBlockState(pos.offset(Direction.EAST));
        BlockState westState = world.getBlockState(pos.offset(Direction.WEST));
        BlockState southState = world.getBlockState(pos.offset(Direction.SOUTH));
        BlockState northState = world.getBlockState(pos.offset(Direction.NORTH));

        eastState = checkDirection(eastState, value, CopperWire.SOUTH, CopperWire.NORTH, CopperWire.EAST, CopperWire.WEST);
        westState = checkDirection(westState, value, CopperWire.NORTH, CopperWire.SOUTH, CopperWire.WEST, CopperWire.EAST);
        southState = checkDirection(southState, value, CopperWire.WEST, CopperWire.EAST, CopperWire.SOUTH, CopperWire.NORTH);
        northState = checkDirection(northState, value, CopperWire. EAST, CopperWire.WEST, CopperWire.NORTH, CopperWire.SOUTH);

        world.setBlockState(pos.offset(Direction.EAST), eastState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.WEST), westState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.SOUTH), southState, Block.NOTIFY_ALL);
        world.setBlockState(pos.offset(Direction.NORTH), northState, Block.NOTIFY_ALL);
    }

    private BlockState checkDirection(BlockState state, boolean value, BooleanProperty... hasDirection)
    {
        if (!(state.getBlock() instanceof CopperWire)) return state;

        Direction facing = state.get(FACING);
        return switch (facing)
        {
            case Direction.EAST -> state.with(hasDirection[0], value);
            case Direction.WEST -> state.with(hasDirection[1], value);
            case Direction.SOUTH -> state.with(hasDirection[2], value);
            case Direction.NORTH -> state.with(hasDirection[3], value);
            default -> state;
        };
    }

    protected void onBlockAddedOld(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        // 주변 좌표 기준 블록 상태 가져오기
        BlockState eastState = world.getBlockState(new BlockPos(pos.getX()+1, pos.getY(), pos.getZ()));
        BlockState westState = world.getBlockState(new BlockPos(pos.getX()-1, pos.getY(), pos.getZ()));
        BlockState southState = world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()+1));
        BlockState northState = world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()-1));

        // 각 블록이 커스텀 블록인지 확인 후 FACING 값 읽기
        if (eastState.getBlock() instanceof CopperWire) {
            Direction eastFacing = eastState.get(CopperWire.FACING);
            if (eastFacing == Direction.SOUTH) { eastState = eastState.with(CopperWire.EAST, true);}
            if (eastFacing == Direction.NORTH) { eastState = eastState.with(CopperWire.WEST, true);}
            if (eastFacing == Direction.EAST) { eastState = eastState.with(CopperWire.SOUTH, true);}
            if (eastFacing == Direction.WEST) { eastState = eastState.with(CopperWire.NORTH, true);}
            world.setBlockState(new BlockPos(pos.getX()+1, pos.getY(), pos.getZ()), eastState, Block.NOTIFY_ALL);
        }

        // WEST
        if (westState.getBlock() instanceof CopperWire) {
            Direction westFacing = westState.get(CopperWire.FACING);
            if (westFacing == Direction.SOUTH) {westState = westState.with(CopperWire.WEST, true);}
            if (westFacing == Direction.NORTH) {westState = westState.with(CopperWire.EAST, true);}
            if (westFacing == Direction.EAST) {westState = westState.with(CopperWire.NORTH, true);}
            if (westFacing == Direction.WEST) {westState = westState.with(CopperWire.SOUTH, true);}
            world.setBlockState(pos.offset(Direction.WEST), westState, 3);
        }

        // SOUTH
        if (southState.getBlock() instanceof CopperWire) {
            Direction southFacing = southState.get(CopperWire.FACING);
            if (southFacing == Direction.EAST) {southState = southState.with(CopperWire.WEST, true);}
            if (southFacing == Direction.WEST) {southState = southState.with(CopperWire.EAST, true);}
            if (southFacing == Direction.SOUTH) {southState = southState.with(CopperWire.SOUTH, true);}
            if (southFacing == Direction.NORTH) {southState = southState.with(CopperWire.NORTH, true);}
            world.setBlockState(pos.offset(Direction.SOUTH), southState, 3);
        }

        // NORTH
        if (northState.getBlock() instanceof CopperWire) {
            Direction northFacing = northState.get(CopperWire.FACING);
            if (northFacing == Direction.EAST) { northState = northState.with(CopperWire.EAST, true); }
            if (northFacing == Direction.WEST) { northState = northState.with(CopperWire.WEST, true); }
            if (northFacing == Direction.SOUTH) { northState = northState.with(CopperWire.NORTH, true); }
            if (northFacing == Direction.NORTH) { northState = northState.with(CopperWire.SOUTH, true); }
            world.setBlockState(pos.offset(Direction.NORTH), northState, 3);
        }
    }

    protected void onStateReplacedOld(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (state.getBlock() == newState.getBlock()) return;

        BlockState eastState = world.getBlockState(new BlockPos(pos.getX()+1, pos.getY(), pos.getZ()));
        BlockState westState = world.getBlockState(new BlockPos(pos.getX()-1, pos.getY(), pos.getZ()));
        BlockState southState = world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()+1));
        BlockState northState = world.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()-1));

        // 각 블록이 커스텀 블록인지 확인 후 FACING 값 읽기
        if (eastState.getBlock() instanceof CopperWire) {
            Direction eastFacing = eastState.get(CopperWire.FACING);
            if (eastFacing == Direction.SOUTH) { eastState = eastState.with(CopperWire.EAST, false);}
            if (eastFacing == Direction.NORTH) { eastState = eastState.with(CopperWire.WEST, false);}
            if (eastFacing == Direction.EAST) { eastState = eastState.with(CopperWire.SOUTH, false);}
            if (eastFacing == Direction.WEST) { eastState = eastState.with(CopperWire.NORTH, false);}
            world.setBlockState(new BlockPos(pos.getX()+1, pos.getY(), pos.getZ()), eastState, 3);
        }

        // WEST
        if (westState.getBlock() instanceof CopperWire) {
            Direction westFacing = westState.get(CopperWire.FACING);
            if (westFacing == Direction.SOUTH) {westState = westState.with(CopperWire.WEST, false);}
            if (westFacing == Direction.NORTH) {westState = westState.with(CopperWire.EAST, false);}
            if (westFacing == Direction.EAST) {westState = westState.with(CopperWire.NORTH, false);}
            if (westFacing == Direction.WEST) {westState = westState.with(CopperWire.SOUTH, false);}
            world.setBlockState(pos.offset(Direction.WEST), westState, 3);
        }

        // SOUTH
        if (southState.getBlock() instanceof CopperWire) {
            Direction southFacing = southState.get(CopperWire.FACING);
            if (southFacing == Direction.EAST) {southState = southState.with(CopperWire.WEST, false);}
            if (southFacing == Direction.WEST) {southState = southState.with(CopperWire.EAST, false);}
            if (southFacing == Direction.SOUTH) {southState = southState.with(CopperWire.SOUTH, false);}
            if (southFacing == Direction.NORTH) {southState = southState.with(CopperWire.NORTH, false);}
            world.setBlockState(pos.offset(Direction.SOUTH), southState, 3);
        }

        // NORTH
        if (northState.getBlock() instanceof CopperWire) {
            Direction northFacing = northState.get(CopperWire.FACING);
            if (northFacing == Direction.EAST) {northState = northState.with(CopperWire.EAST, false);}
            if (northFacing == Direction.WEST) {northState = northState.with(CopperWire.WEST, false);}
            if (northFacing == Direction.SOUTH) {northState = northState.with(CopperWire.NORTH, false);}
            if (northFacing == Direction.NORTH) {northState = northState.with(CopperWire.SOUTH, false);}
            world.setBlockState(pos.offset(Direction.NORTH), northState, 3);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
