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

    // ⚡ 우클릭 감전 이벤트
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
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        //좌표 기준으로 블록 불러오기
        Block EAST = world.getBlockState(new BlockPos(pos.getX()+1, pos.getY() ,pos.getZ())).getBlock();
        Block WEST = world.getBlockState(new BlockPos(pos.getX()-1, pos.getY() ,pos.getZ())).getBlock();
        Block SOUTH = world.getBlockState(new BlockPos(pos.getX(), pos.getY() ,pos.getZ()+1)).getBlock();
        Block NORTH = world.getBlockState(new BlockPos(pos.getX(), pos.getY() ,pos.getZ()-1)).getBlock();

        //CopperWire인지 확인하기
        if(EAST.getName() != this.getName()) {EAST = null;}
        if(WEST.getName() != this.getName()) {WEST = null;}
        if(SOUTH.getName() != this.getName()) {SOUTH = null;}
        if(NORTH.getName() != this.getName()) {NORTH = null;}



    }
}
