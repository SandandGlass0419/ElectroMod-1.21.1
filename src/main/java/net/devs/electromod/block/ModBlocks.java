package net.devs.electromod.block;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.custom.TestBlock;
import net.devs.electromod.block.custom.electro.Battery;
import net.devs.electromod.block.custom.electro.CopperWire;
import net.devs.electromod.block.custom.electro.GoldWire;
import net.devs.electromod.block.custom.electro.Wire;
import net.devs.electromod.block.custom.magnetic.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonBehavior;
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
                    .strength(1f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()));

    public static final Block GOLDEN_COIL = addBlock("golden_coil",
            new GoldenCoilBlock(AbstractBlock.Settings.create()
                    .strength(1f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()));

    public static final Block COPPER_COIL = addBlock("copper_coil",
            new CopperCoilBlock(AbstractBlock.Settings.create()
                    .strength(1f)
                    .sounds(BlockSoundGroup.COPPER)
                    .nonOpaque()));

    public static final Block MAGNET_BLOCK = addBlock("magnet_block",
            new MagnetBlock(AbstractBlock.Settings.create()
                    .strength(3.5f)
                    .sounds(BlockSoundGroup.LODESTONE)
                    .pistonBehavior(PistonBehavior.BLOCK)));

    public static final Block MAGNETIC_DETECTOR = addBlock("magnetic_detector",
            new MagneticDetector(AbstractBlock.Settings.create()
                    .strength(0f)
                    .sounds(BlockSoundGroup.COPPER)
                    .nonOpaque()));

    // blocks from electro
    public static final Block COPPER_WIRE = addBlock("copper_wire",
            new CopperWire(AbstractBlock.Settings.create()
                    .strength(1f)
                    .sounds(BlockSoundGroup.COPPER)
                    .nonOpaque()
                    .requiresTool()));

    public static final Block WIRE = addBlock("wire",
            new Wire(AbstractBlock.Settings.create()
                    .strength(1f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .requiresTool()));

    public static final Block GOLDEN_WIRE = addBlock("gold_wire",
            new GoldWire(AbstractBlock.Settings.create()
                    .strength(1f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .requiresTool()));

    public static final Block BATTERY = addBlock("battery",
            new Battery(AbstractBlock.Settings.create()
                    .strength(1f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .requiresTool()));

    public static void registerModBlocks()
    {
        ElectroMod.LOGGER.info("Registering Mod Blocks (" + ElectroMod.MOD_ID + ")");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries ->
        {
            // magnetic blocks
            entries.add(IRON_COIL);
            entries.add(GOLDEN_COIL);
            entries.add(COPPER_COIL);
            entries.add(MAGNET_BLOCK);
            entries.add(MAGNETIC_DETECTOR);

            // electro blocks
            entries.add(COPPER_WIRE);
            entries.add(GOLDEN_WIRE);
            entries.add(WIRE);
            entries.add(BATTERY);
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
