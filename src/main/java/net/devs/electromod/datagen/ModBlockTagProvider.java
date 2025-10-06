package net.devs.electromod.datagen;

import net.devs.electromod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    @Override
    public void configure(RegistryWrapper.WrapperLookup wrapperLookup)
    {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE) // tags for adding blocks that can be mined with a pickaxe
                //magnetic
                .add(ModBlocks.IRON_COIL)
                .add(ModBlocks.GOLDEN_COIL)
                .add(ModBlocks.COPPER_COIL)
                .add(ModBlocks.MAGNET_BLOCK)
                //electro
                .add(ModBlocks.COPPER_WIRE);
    }
}
