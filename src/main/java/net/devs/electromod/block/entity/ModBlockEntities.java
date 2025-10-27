package net.devs.electromod.block.entity;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.entity.custom.electro.AcDcConvertEntity;
import net.devs.electromod.block.entity.custom.electro.PNDiodeEntity;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.devs.electromod.block.entity.custom.magnetic.CoilBlockEntity;
import net.devs.electromod.block.entity.custom.magnetic.MagnetBlockEntity;
import net.devs.electromod.block.entity.custom.magnetic.MagneticDetectorEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities
{
    // magnetic
    public static final BlockEntityType<CoilBlockEntity> COIL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "coil_be"),
                    BlockEntityType.Builder.create(CoilBlockEntity::new,
                            ModBlocks.IRON_COIL,
                            ModBlocks.GOLDEN_COIL,
                            ModBlocks.COPPER_COIL
                    ).build());

    public static final BlockEntityType<MagnetBlockEntity> MAGNET_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "magnet_be"),
                    BlockEntityType.Builder.create(MagnetBlockEntity::new,
                            ModBlocks.MAGNET_BLOCK
                    ).build());

    public static final BlockEntityType<MagneticDetectorEntity> MAGNETIC_DETECTOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "magnetic_detector_be"),
                    BlockEntityType.Builder.create(MagneticDetectorEntity::new,
                            ModBlocks.MAGNETIC_DETECTOR
                    ).build());
    // electro
    public static final BlockEntityType<WireBlockEntity> WIRE_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "wire_be"),
                    BlockEntityType.Builder.create(WireBlockEntity::new,
                            ModBlocks.COPPER_WIRE,
                            ModBlocks.WIRE,
                            ModBlocks.GOLDEN_WIRE
                    ).build());

    public static final BlockEntityType<AcDcConvertEntity> ACDC_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "acdc_be"),
                    BlockEntityType.Builder.create(AcDcConvertEntity::new,
                            ModBlocks.ACDC_CONVERTER
                    ).build());

 public static final BlockEntityType<PNDiodeEntity> PN_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(ElectroMod.MOD_ID, "pn_be"),
                    BlockEntityType.Builder.create(PNDiodeEntity::new,
                            ModBlocks.PN_DIODE
                    ).build());





    public static void registerModBlockEntities()
    {
        ElectroMod.LOGGER.info("Registering BlockEntities");
    }
}
