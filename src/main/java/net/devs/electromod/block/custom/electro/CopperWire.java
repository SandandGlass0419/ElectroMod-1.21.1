package net.devs.electromod.block.custom.electro;


import com.mojang.serialization.MapCodec;
import net.devs.electromod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperWire extends Block {

    public static final DirectionProperty FACING = Properties.FACING;

    public CopperWire(Settings settings)
    {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return super.getCodec();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if(!player.getMainHandStack().getItem().equals(ModItems.RUBBER_GLOVES)) //dies when you click it without rubber gloves
        {
            BlockPos blockpos = player.getBlockPos();

            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,world);
            lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(),0,0);

            world.spawnEntity(lightning);

            player.kill();
            player.sendMessage(Text.literal("Oops!"), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        BlockPos blockpos = entity.getBlockPos();

        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,world);
        lightning.refreshPositionAndAngles(blockpos.getX(), blockpos.getY(), blockpos.getZ(),0,0);

        world.spawnEntity(lightning);

        entity.kill();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
}
