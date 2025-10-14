package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.ElectroMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.*;

public class MagneticForceInteractor
{
    private static final Map<RegistryKey<DimensionType>, Map<BlockPos, Integer>> magneticBlockPos = new HashMap<>();

    public static void subscribeMagneticBlock(World world, BlockPos pos, int redstonePower)
    {
        magneticBlockPos.get(getDimensionKey(world)).put(pos, redstonePower);
        ElectroMod.LOGGER.info("added on: " + pos + ", power: " + redstonePower);
    }

    public static void unsubscribeMagneticBlock(World world, BlockPos pos)
    {
        magneticBlockPos.get(getDimensionKey(world)).remove(pos);
        ElectroMod.LOGGER.info("removed on (magnetic): " + pos);
    }

    private static void initMagneticMap()
    {
        magneticBlockPos.clear();
        magneticBlockPos.put(DimensionTypes.OVERWORLD, new HashMap<>());
        magneticBlockPos.put(DimensionTypes.THE_NETHER, new HashMap<>());
        magneticBlockPos.put(DimensionTypes.THE_END, new HashMap<>());
        //ElectroMod.LOGGER.info("cleared!");
    }

    private static final Map<RegistryKey<DimensionType>, Map<BlockPos, Set<BlockPos>>> detectorBlockPos = new HashMap<>();

    public static void subscribeDetectorBlock(World world, BlockPos detectorPos, Set<BlockPos> magneticPoses)
    {
        detectorBlockPos.get(getDimensionKey(world)).put(detectorPos, magneticPoses);
        ElectroMod.LOGGER.info("added on(detector): " + detectorPos);
    }

    public static void unsubscribeDetectorBlock(World world, BlockPos detectorPos)
    {
        detectorBlockPos.get(getDimensionKey(world)).remove(detectorPos);
        ElectroMod.LOGGER.info("removed on(detector): " + detectorPos);
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

        for (BlockPos magneticPos : magneticBlockPos.get(getDimensionKey(world)).keySet())
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

    public static Vector<Set<BlockPos>> getAllPositions(BlockPos pos, ForceProfile forceProfile)
    {
        Vector<Set<BlockPos>> Positions = new Vector<>(Arrays.asList(new HashSet<>(), new HashSet<>(), new HashSet<>()));

        for (int power = 0; power < 3; power++)
        {
            Set<MVec3i> deltas = new HashSet<>();

            deltas.addAll(forceProfile.headProfile().get(power));
            deltas.addAll(forceProfile.bodyProfile().get(power));
            deltas.addAll(forceProfile.tailProfile().get(power));

            Positions.set(power, MVec3i.add(deltas, pos));
        }

        return Positions;
    }
}
