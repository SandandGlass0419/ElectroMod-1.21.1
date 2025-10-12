package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MagneticForceInteractor
{
    private static Map<RegistryKey<DimensionType>, Set<BlockPos>> magneticBlockPos = new HashMap<>();

    public static void subscribeBlock(RegistryKey<DimensionType> dimension, BlockPos pos)
    {
        magneticBlockPos.get(dimension).add(pos);
        //ElectroMod.LOGGER.info("added on: " + pos);
    }

    public static void unsubscribeBlock(RegistryKey<DimensionType> dimension, BlockPos pos)
    {
        magneticBlockPos.get(dimension).remove(pos);
        //ElectroMod.LOGGER.info("removed on: " + pos);
    }

    public static void initPosMap()
    {
        magneticBlockPos.clear();
        magneticBlockPos.put(DimensionTypes.OVERWORLD, new HashSet<>());
        magneticBlockPos.put(DimensionTypes.THE_NETHER, new HashSet<>());
        magneticBlockPos.put(DimensionTypes.THE_END, new HashSet<>());
        //ElectroMod.LOGGER.info("cleared!");
    }
}
