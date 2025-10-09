package net.devs.electromod.item.custom.electro;

import net.devs.electromod.block.custom.electro.WireBlock;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class RubberGloves extends Item {
    public RubberGloves(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // 메인핸드인지 체크
        if (context.getHand() != net.minecraft.util.Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }

        World world = context.getWorld();
        if (world.isClient()) return ActionResult.FAIL;

        Block block = world.getBlockState(context.getBlockPos()).getBlock();
        BlockEntity be = world.getBlockEntity(context.getBlockPos());

        if (block instanceof WireBlock && be instanceof WireBlockEntity wireEntity) {
            wireEntity.addElectrocity(100); // 전류 100으로 설정
            if (context.getPlayer() != null) {
                context.getPlayer().sendMessage(Text.literal("ELECTRICITY SET TO 100!"), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

}
