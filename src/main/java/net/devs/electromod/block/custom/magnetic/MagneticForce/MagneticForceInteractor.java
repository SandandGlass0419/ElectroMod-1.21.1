package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MagneticForceInteractor
{
    private static Map<RegistryKey<DimensionType>, Set<BlockPos>> magneticBlockPos = new HashMap<>();

    public static void subscribeMagneticBlock(World world, BlockPos pos)
    {
        magneticBlockPos.get(getDimensionKey(world)).add(pos);
        //ElectroMod.LOGGER.info("added on: " + pos);
    }

    public static void unsubscribeMagneticBlock(World world, BlockPos pos)
    {
        magneticBlockPos.get(getDimensionKey(world)).remove(pos);
        //ElectroMod.LOGGER.info("removed on: " + pos);
    }

    private static void initMagneticMap()
    {
        magneticBlockPos.clear();
        magneticBlockPos.put(DimensionTypes.OVERWORLD, new HashSet<>());
        magneticBlockPos.put(DimensionTypes.THE_NETHER, new HashSet<>());
        magneticBlockPos.put(DimensionTypes.THE_END, new HashSet<>());
        //ElectroMod.LOGGER.info("cleared!");
    }

    private static Map<RegistryKey<DimensionType>, Map<BlockPos, Set<BlockPos>>> detectorBlockPos = new HashMap<>();

    public static void subscribeDetectorBlock(World world, BlockPos detectorPos, Set<BlockPos> magneticPoses)
    {
        detectorBlockPos.get(getDimensionKey(world)).put(detectorPos, magneticPoses);
        //ElectroMod.LOGGER.info("added on: " + pos);
    }

    public static void unsubscribeDetectorBlock(World world, BlockPos detectorPos)
    {
        detectorBlockPos.get(getDimensionKey(world)).remove(detectorPos);
        //ElectroMod.LOGGER.info("removed on: " + pos);
    }

    private static void initDetectorMap()
    {
        detectorBlockPos.clear();
        detectorBlockPos.put(DimensionTypes.OVERWORLD, new HashMap<>());
        detectorBlockPos.put(DimensionTypes.THE_NETHER, new HashMap<>());
        detectorBlockPos.put(DimensionTypes.THE_END, new HashMap<>());
    }

    public static void initPosMap(MinecraftServer server, ServerWorld world)
    {
        initMagneticMap();
        initDetectorMap();
    }

    public static RegistryKey<DimensionType> getDimensionKey(World world)
    {
        var key = world.getDimensionEntry().getKey();
        return key.orElse(null);
    }

    public static Set<BlockPos> detectorPlacementCheck(World world, BlockPos detectorPos)
    {
        Set<BlockPos> passedPoses = new HashSet<>();

        for (BlockPos magneticPos : magneticBlockPos.get(getDimensionKey(world)))
        {
            if (!RelativeDistanceCondition(detectorPos, magneticPos)) continue;

            passedPoses.add(magneticPos);
        }

        return passedPoses;
    }

    public static Set<BlockPos> testPlacementCheck(World world, BlockPos pos)
    {
        return detectorBlockPos.get(getDimensionKey(world)).get(pos);
    }

    public static boolean RelativeDistanceCondition(BlockPos detectorPos, BlockPos magneticPos)
    {
        return Math.abs(detectorPos.getX() - magneticPos.getX()) <= ForceProfile.minmumDistance &&
                Math.abs(detectorPos.getY() - magneticPos.getY()) <= ForceProfile.minmumDistance &&
                Math.abs(detectorPos.getZ() - magneticPos.getZ()) <= ForceProfile.minmumDistance;
    }

    public static boolean hasPowerCondition(World world, BlockPos magneticPos)
    {
        if (!(world.getBlockEntity(magneticPos) instanceof AbstractMagneticBlockEntity magneticBE)) return false;

        return magneticBE.getMagneticForce() != 0;
    }
}
