package net.devs.electromod.block;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.custom.TestBlock;
import net.devs.electromod.block.custom.magnetic.CopperCoilBlock;
import net.devs.electromod.block.custom.magnetic.GoldenCoilBlock;
import net.devs.electromod.block.custom.magnetic.IronCoilBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks
{
    // test block for testing block adding
    public static final Block TEST_BLOCK = addBlock("test_block",
            new TestBlock(AbstractBlock.Settings.create()
                    .strength(0f)
                    .sounds(BlockSoundGroup.HONEY)));

    // blocks from magnetic
    public static final Block IRON_COIL = addBlock("iron_coil",
            new IronCoilBlock(AbstractBlock.Settings.create()
                    .strength(2f)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block GOLDEN_COIL = addBlock("golden_coil",
            new GoldenCoilBlock(AbstractBlock.Settings.create()
                    .strength(2f)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.METAL)));

    public static final Block COPPER_COIL = addBlock("copper_coil",
            new CopperCoilBlock(AbstractBlock.Settings.create()
                    .strength(2f)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.COPPER)));

    public static void registerModBlocks()
    {
        ElectroMod.LOGGER.info("Registering Mod Blocks (" + ElectroMod.MOD_ID + ")");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries ->
        {
            entries.add(IRON_COIL);
            entries.add(GOLDEN_COIL);
            entries.add(COPPER_COIL);
        });
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
