package net.devs.electromod.datagen;

import net.devs.electromod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider
{
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup)
    {
        super(dataOutput, registryLookup);
    }

    // generates loottable for blocks. ex) mine diamond block -> drop diamond block
    // mine diamond ore -> drop diamond(s)
    // please add ALL blocks (that has a drop)

    @Override
    public void generate()
    {
        // magnetic
        addDrop(ModBlocks.IRON_COIL);
    }
}
