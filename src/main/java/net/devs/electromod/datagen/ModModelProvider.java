package net.devs.electromod.datagen;

import net.devs.electromod.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider
{
    public ModModelProvider(FabricDataOutput output)
    {
        super(output);
    }

    // generates block model json files. please add blocks with simple form. (cube...)
    // + mention blocks not registered here

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
    {
        // magnetic
        // iron_coil (custom json)
        // golder_coil (custom json)
        // copper_coil (custom json)
        // magnet_block (custom json)
        // magnetic_detector (custom json)
        // electro
        // copper_wire (custom json)
        // golden_wire (custom json)
        // wire (custom json)
        // battery (custom json)
        // acdc converter(custom json)
    }

    // generates item model json files. please add ALL items using method

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator)
    {
        // magnetic
        itemModelGenerator.register(ModItems.MAGNET_ITEM, Models.HANDHELD);
        //electro
        itemModelGenerator.register(ModItems.ELECTRO_ITEM, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RUBBER_GLOVES, Models.HANDHELD);
        itemModelGenerator.register(ModItems.ELECTRO_STAFF, Models.HANDHELD);
    }
}
