package net.devs.electromod.item;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups
{
    // items to add in Magnetic Items item group
    public static final ItemGroup MAGNETIC_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(ElectroMod.MOD_ID, "magnetic_items"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.MAGNET_ITEM))
                    .displayName(Text.translatable("itemGroup.electromod.magnetic_items"))
                    .entries(((displayContext, entries) ->
                    {
                        // magnetic item
                        entries.add(ModItems.MAGNET_ITEM);

                        // magnetic block
                        entries.add(ModBlocks.IRON_COIL);
                        entries.add(ModBlocks.GOLDEN_COIL);
                        entries.add(ModBlocks.COPPER_COIL);
                        entries.add(ModBlocks.MAGNET_BLOCK);
                        entries.add(ModBlocks.MAGNETIC_DETECTOR);
                    })).build());

    public static final ItemGroup ELECTRO_ITEM_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(ElectroMod.MOD_ID, "electro_items"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.ELECTRO_ITEM))
                    .displayName(Text.translatable("itemGroup.electromod.electro_items"))
                    .entries(((displayContext, entries) ->
                    {
                        // electro item
                        //entries.add(ModItems.ELECTRO_ITEM);

                        // electro block
                        entries.add(ModBlocks.COPPER_WIRE);
                        entries.add(ModBlocks.GOLDEN_WIRE);
                        entries.add(ModBlocks.WIRE);
                        entries.add(ModBlocks.ACDC_CONVERTER);
                        entries.add(ModBlocks.ELECTRO_DECTOR);
                        entries.add(ModBlocks.PN_DIODE);

                        entries.add(ModItems.RUBBER_GLOVES);
                        entries.add(ModItems.ELECTRO_STAFF);
                        entries.add(ModItems.ELECTRO_ITEM);

                    })).build());



    public static void registerModItemGroups()
    {
        ElectroMod.LOGGER.info("Registering Mod Item Groups (" + ElectroMod.MOD_ID + ")");
    }
}
