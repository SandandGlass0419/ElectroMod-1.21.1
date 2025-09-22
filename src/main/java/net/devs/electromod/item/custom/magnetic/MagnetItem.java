package net.devs.electromod.item.custom.magnetic;

import net.devs.electromod.components.ModDataComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class MagnetItem extends Item
{
    public MagnetItem(Settings settings)
    {
        super(settings.maxCount(1));
    }

    @Override public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.FAIL;

        Block block = world.getBlockState(context.getBlockPos()).getBlock();

        if (block == Blocks.LODESTONE)
        {
            int NewForce = updateForce(context.getStack());
            displayForce(context.getPlayer(), NewForce);
            world.playSound(null, context.getBlockPos(), SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.BLOCKS);

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private int updateForce(ItemStack itemStack)
    {
        int NewForce = getNewForce(itemStack, 1);

        itemStack.set(ModDataComponentTypes.MAGNET_FORCE, NewForce);
        return NewForce;
    }

    private int getNewForce(ItemStack itemStack, int increment)
    {
        int current_force = itemStack.getOrDefault(ModDataComponentTypes.MAGNET_FORCE, ModDataComponentTypes.max_force);
        int new_force = current_force >= ModDataComponentTypes.max_force ?
                ModDataComponentTypes.min_force : current_force + Math.abs(increment);

        if (!(ModDataComponentTypes.min_force <= new_force && new_force <= ModDataComponentTypes.max_force))
        { return current_force; }

        return new_force;
    }

    private void displayForce(PlayerEntity player, int force)
    {
        player.sendMessage(Text.literal("Current Force: " + force), true);
    }
}
