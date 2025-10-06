package net.devs.electromod.block.custom.electro;


import net.devs.electromod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
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

        if(!player.getMainHandStack().getItem().equals(ModItems.RUBBER_GLOVES))
        {
            player.setHealth(0f);
            player.sendMessage(Text.literal("Oops!"), true);
            player.playSound(soundGroup.getHitSound());
        }


        return ActionResult.SUCCESS;
    }
}
