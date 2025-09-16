package net.devs.electromod.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestItem extends Item
{
    public TestItem(Settings settings)
    {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
    {
        World world = user.getWorld();

        if (world.isClient()) return ActionResult.FAIL;

        BlockPos pos = entity.getBlockPos();

        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,world);
        lightning.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(),0,0);

        world.spawnEntity(lightning);

        return ActionResult.SUCCESS;
    }
}
