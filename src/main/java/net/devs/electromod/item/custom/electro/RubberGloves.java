package net.devs.electromod.item.custom.electro;

import net.devs.electromod.block.custom.electro.WireBlock;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
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

        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);
        World world = context.getWorld();
        if (world.isClient()) return ActionResult.FAIL;

        Block block = state.getBlock();
        BlockEntity be = world.getBlockEntity(pos);

        if (block instanceof WireBlock && be instanceof WireBlockEntity wireEntity) {

            if (context.getPlayer() != null) {
                // 플레이어 게임모드 확인
                boolean isCreative = context.getPlayer().getAbilities().creativeMode;
                boolean isSurvival = !context.getPlayer().getAbilities().creativeMode && !context.getPlayer().isSpectator();

                if (!isCreative && !isSurvival) {
                    // 어드벤처, 스펙테이터 등에서는 작동하지 않음
                    context.getPlayer().sendMessage(Text.literal("Cannot modify electricity in this game mode!"), true);
                    return ActionResult.FAIL;
                }

                // Shift 눌림 여부 체크
                if (context.getPlayer().isSneaking()) {
                    wireEntity.updateElectricity(0); // 전류 0으로 초기화
                    context.getPlayer().sendMessage(Text.literal("ELECTRICITY RESET TO 0!"), true);
                } else {
                    wireEntity.updateElectricity(100); // 전류 100으로 설정
                    context.getPlayer().sendMessage(Text.literal("ELECTRICITY SET TO 100!"), true);
                }

                return ActionResult.SUCCESS;
            }

        }

        return ActionResult.FAIL;
    }


}
