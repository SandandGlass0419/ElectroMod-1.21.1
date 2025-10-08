package net.devs.electromod;
import net.devs.electromod.block.ModBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.api.ClientModInitializer;

public class ElectroModClient implements ClientModInitializer
{
    @Override public void onInitializeClient()
    {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ACDC_CONVERTER, RenderLayer.getCutout());
    }
}
