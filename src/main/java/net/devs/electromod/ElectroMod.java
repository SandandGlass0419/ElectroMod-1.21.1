package net.devs.electromod;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.components.ModDataComponentTypes;
import net.devs.electromod.item.ModItemGroups;
import net.devs.electromod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElectroMod implements ModInitializer
{
    public static final String MOD_ID = "electromod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
    {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerModItemGroups();
        ModDataComponentTypes.registerModDataComponentTypes();
        ModBlockEntities.registerModBlockEntities();
	}
}