package net.devs.electromod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider)
    {
        super(output, completableFuture, blockTagProvider);
    }

    // datagen class for creating item tags. item tags are sets of blocks that can be used like lists of blocks in
    // a constant form. custom tags are used in this case.

    @Override
    public void configure(RegistryWrapper.WrapperLookup wrapperLookup)
    {

    }
}
