package net.devs.electromod.block;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.custom.TestBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks
{
    public static final Block TEST_BLOCK = addBlock("test_block",
            new TestBlock(AbstractBlock.Settings.create()
                    .strength(0f)
                    .sounds(BlockSoundGroup.HONEY)));

    // blocks from magnetic

    public static void registerModBlocks()
    {
        ElectroMod.LOGGER.info("Registering Mod Blocks (" + ElectroMod.MOD_ID + ")");
    }

    private static void addBlockItem(String name, Block block)
    {
        Registry.register(Registries.ITEM, Identifier.of(ElectroMod.MOD_ID, name),
        new BlockItem(block, new Item.Settings()));
    }

    private static Block addBlock(String name, Block block)
    {
        addBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(ElectroMod.MOD_ID, name), block);
    }
}
