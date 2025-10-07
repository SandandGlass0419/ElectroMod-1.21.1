package net.devs.electromod.block.entity;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.devs.electromod.block.entity.custom.magnetic.CoilBlockEntity;
import net.devs.electromod.block.entity.custom.magnetic.MagneticDetectorEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities
{
    //magnetic
    public static final BlockEntityType<CoilBlockEntity> COIL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "coil_be"),
                    BlockEntityType.Builder.create(CoilBlockEntity::new,
                            ModBlocks.IRON_COIL,
                            ModBlocks.GOLDEN_COIL,
                            ModBlocks.COPPER_COIL
                    ).build());

    public static final BlockEntityType<MagneticDetectorEntity> DETECTOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "detector_be"),
                    BlockEntityType.Builder.create(MagneticDetectorEntity::new,
                            ModBlocks.MAGNETIC_DETECTOR
                    ).build());
    //electro
    public static final BlockEntityType<WireBlockEntity> WIRE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "detector_be"),
                    BlockEntityType.Builder.create(WireBlockEntity::new,
                            ModBlocks.COPPER_WIRE
                    ).build());



    public static void registerModBlockEntities()
    {
        ElectroMod.LOGGER.info("Registering BlockEntities (" + ElectroMod.MOD_ID + ")");
    }
}
