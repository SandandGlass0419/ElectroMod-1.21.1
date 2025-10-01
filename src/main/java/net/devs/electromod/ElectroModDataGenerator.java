package net.devs.electromod;

import net.devs.electromod.datagen.ModBlockTagProvider;
import net.devs.electromod.datagen.ModLootTableProvider;
import net.devs.electromod.datagen.ModModelProvider;
import net.devs.electromod.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ElectroModDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModBlockTagProvider::new);
        //pack.addProvider(ModItemTagProvider::new); // no item tag yet
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
	}
}
