package net.devs.electromod.item;

import net.devs.electromod.ElectroMod;
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
            Identifier.of(ElectroMod.MOD_ID,"magnetic_items"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.MAGNET_ITEM))
                    .displayName(Text.translatable("itemGroup.electromod.magnetic_items"))
                    .entries(((displayContext, entries) ->
                    {
                        entries.add(ModItems.MAGNET_ITEM);

                    })).build());



    public static void registerModItemGroups()
    {
        ElectroMod.LOGGER.info("Registering Mod Item Groups (" + ElectroMod.MOD_ID + ")");
    }
}
