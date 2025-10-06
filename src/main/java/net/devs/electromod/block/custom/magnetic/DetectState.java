package net.devs.electromod.block.custom.magnetic;

import net.devs.electromod.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;

public enum DetectState implements StringIdentifiable
{
    EMPTY("empty", null),
    IRON_COIL("iron", ModBlocks.IRON_COIL.asItem()),
    GOLDEN_COIL("gold", ModBlocks.GOLDEN_COIL.asItem()),
    COPPER_COIL("copper", ModBlocks.COPPER_COIL.asItem());

    private final String name;
    private final Item storedItem;

    DetectState(String name, Item storedItem)
    {
        this.name = name;
        this.storedItem = storedItem;
    }

    @Override public String asString() { return this.name; }

    public ItemStack getStoredItem() {
        return storedItem != null ? new ItemStack(storedItem) : ItemStack.EMPTY;
    }

    public static DetectState findStateWith(Item item)
    {
        for (DetectState state : values())
        {
            if (state.storedItem == item) return state;
        }

        return null;
    }
}
