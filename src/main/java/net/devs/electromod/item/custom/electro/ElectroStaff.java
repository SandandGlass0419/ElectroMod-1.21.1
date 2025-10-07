package net.devs.electromod.item.custom.electro;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElectroStaff extends Item {
    public ElectroStaff(Settings settings) {
        super(settings);
    }


    //선택한 도선의 전기량 확인
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.FAIL;
        Block block = world.getBlockState(context.getBlockPos()).getBlock();

        if(block.equals(ModBlocks.COPPER_WIRE))
        {
            WireBlockEntity wireEntity = (WireBlockEntity) world.getBlockEntity(context.getBlockPos());
            assert context.getPlayer() != null;
            assert wireEntity != null;
            context.getPlayer().sendMessage(Text.literal("ELECTRICITY : " + wireEntity.getStoredValue()), true);

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    //최강의 번개다 그지 깽꺵이들아!!!!!
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
    {
        World world = user.getWorld();
        if (world.isClient()) return ActionResult.FAIL;

        BlockPos pos = entity.getBlockPos();

        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,world);
        lightning.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(),0,0);

        world.spawnEntity(lightning);

        user.sendMessage(Text.literal("TAKE THE POWER OF LIGHTNING!!!!!"), true);

        return ActionResult.SUCCESS;
    }
}
