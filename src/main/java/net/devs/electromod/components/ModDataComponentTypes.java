package net.devs.electromod.components;

import net.devs.electromod.ElectroMod;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes
{
    // mod defined component types - see DataComponentTypes.java in net.minecraft

    public static final int min_power = 1; public static final int max_power = 15;
    public static final ComponentType<Integer> MAGNETIC_POWER = addComponentType(
            "magnet_force", builder -> builder.codec(Codecs.rangedInt(min_power, max_power)).packetCodec(PacketCodecs.VAR_INT));


    // helper method to create new component type
    private static <T>ComponentType<T> addComponentType(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator)
    {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ElectroMod.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerModDataComponentTypes()
    {
        ElectroMod.LOGGER.info("Registering Mod Data Component Types (" + ElectroMod.MOD_ID + ")");
    }
}
