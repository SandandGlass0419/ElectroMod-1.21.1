package net.devs.electromod.block.custom.electro;


import net.devs.electromod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperWire extends Block {


    public CopperWire(Settings settings)
    {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {

        if(!player.getMainHandStack().getItem().equals(ModItems.RUBBER_GLOVES)) //dies when you click it without rubber gloves
        {
            player.kill();
            player.sendMessage(Text.literal("Oops!"), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        entity.kill();
    }
}
