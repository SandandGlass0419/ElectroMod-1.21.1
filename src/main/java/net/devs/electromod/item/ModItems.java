package net.devs.electromod.item;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.item.custom.TestItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{
    public static final Item TEST_ITEM
            = addItem("test_item", new TestItem(new Item.Settings())); // test item for adding item

    public static void registerModItems()
    {
        ElectroMod.LOGGER.info("Registering Mod Items (" + ElectroMod.MOD_ID + ")");
    }

    private static Item addItem(String name, Item item) // same as registerItem on tutorial
    {
        return Registry.register(Registries.ITEM, Identifier.of(ElectroMod.MOD_ID, name), item);
    }
}
