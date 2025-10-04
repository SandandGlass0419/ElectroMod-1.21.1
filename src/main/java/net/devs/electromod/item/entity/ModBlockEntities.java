package net.devs.electromod.item.entity;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.item.entity.custom.magnetic.CoilBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities
{
    public static final BlockEntityType<CoilBlockEntity> IRON_COIL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "iron_coil_be"),
                    BlockEntityType.Builder.create(CoilBlockEntity::new, ModBlocks.IRON_COIL).build());

    public static void registerModBlockEntities()
    {
        ElectroMod.LOGGER.info("Registering BlockEntities (" + ElectroMod.MOD_ID + ")");
    }
}
