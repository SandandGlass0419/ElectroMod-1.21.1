package net.devs.electromod.item;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.item.custom.TestItem;
import net.devs.electromod.item.custom.electro.ElectroStaff;
import net.devs.electromod.item.custom.electro.RubberGloves;
import net.devs.electromod.item.custom.magnetic.MagnetItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{
    // test item for item adding test
    public static final Item TEST_ITEM
            = addItem("test_item", new TestItem(new Item.Settings()));

    // items from magnetic
    public static final Item MAGNET_ITEM
            = addItem("magnet_item", new MagnetItem(new Item.Settings()));

    // items from electro
    public static final Item ELECTRO_ITEM
            = addItem("electro_item", new Item(new Item.Settings()));

    public static final Item RUBBER_GLOVES
            = addItem("rubber_gloves", new RubberGloves(new Item.Settings()));

    public static final  Item ELECTRO_STAFF
            = addItem("electro_staff", new ElectroStaff(new Item.Settings()));

    public static void registerModItems()
    {
        ElectroMod.LOGGER.info("Registering Mod Items (" + ElectroMod.MOD_ID + ")");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries ->
        {
            // magnetic
            entries.add(MAGNET_ITEM);

            // electro
            entries.add(ELECTRO_ITEM);
            entries.add(RUBBER_GLOVES);
            entries.add(ELECTRO_ITEM);
        });
    }

    private static Item addItem(String name, Item item) // same as registerItem on tutorial
    {
        return Registry.register(Registries.ITEM, Identifier.of(ElectroMod.MOD_ID, name), item);
    }
}
