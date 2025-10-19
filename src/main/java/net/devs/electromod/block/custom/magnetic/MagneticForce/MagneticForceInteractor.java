package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.ElectroMod;
import net.devs.electromod.block.custom.magnetic.CopperCoilBlock;
import net.devs.electromod.block.custom.magnetic.GoldenCoilBlock;
import net.devs.electromod.block.custom.magnetic.IronCoilBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MagneticForceInteractor
{
    private static final Map<RegistryKey<DimensionType>, Map<BlockPos, MagneticField>> magneticBlockPosMap = new HashMap<>();

    public static void subscribeMagneticBlock(World world, BlockPos pos, MagneticField field)
    {
        magneticBlockPosMap.get(getDimensionKey(world)).put(pos, field);   // add + edit

        ElectroMod.LOGGER.info("added on: {}, power: {}, direction: {}", pos, field.getMagneticPower(), field.getForceDirection().toString());

        updateValidPos(world, pos);
    }

    public static void unsubscribeMagneticBlock(World world, BlockPos pos)
    {
        magneticBlockPosMap.get(getDimensionKey(world)).remove(pos);

        ElectroMod.LOGGER.info("removed on (magnetic): {}", pos);

        removeValidPos(world, pos);
    }

    @Nullable
    public static MagneticField getField(World world, BlockPos magneticPos)
    {
        return magneticBlockPosMap.get(getDimensionKey(world)).get(magneticPos);
    }

    private static void initMagneticMap()
    {
        magneticBlockPosMap.clear();
        magneticBlockPosMap.put(DimensionTypes.OVERWORLD, new HashMap<>());
        magneticBlockPosMap.put(DimensionTypes.THE_NETHER, new HashMap<>());
        magneticBlockPosMap.put(DimensionTypes.THE_END, new HashMap<>());
        //ElectroMod.LOGGER.info("cleared!");
    }
                                                          // detector, fields
    private static final Map<RegistryKey<DimensionType>, Map<BlockPos, BlockField>> detectorBlockPosMap = new HashMap<>();

    public static void subscribeDetectorBlock(World world, BlockPos detectorPos, BlockField blockField)
    {
        ElectroMod.LOGGER.info("subscribeDetectorBlock");
        detectorBlockPosMap.get(getDimensionKey(world)).put(detectorPos, blockField);
        ElectroMod.LOGGER.info("added on(detector): {}", detectorPos);
    }

    public static void unsubscribeDetectorBlock(World world, BlockPos detectorPos)
    {
        detectorBlockPosMap.get(getDimensionKey(world)).remove(detectorPos);
        ElectroMod.LOGGER.info("removed on(detector): {}", detectorPos);
    }

    @Nullable
    public static BlockField getBlockField(World world, BlockPos detectorPos)
    {
        return detectorBlockPosMap.get(getDimensionKey(world)).get(detectorPos);
    }

    private static void initDetectorMap()
    {
        detectorBlockPosMap.clear();
        detectorBlockPosMap.put(DimensionTypes.OVERWORLD, new HashMap<>());
        detectorBlockPosMap.put(DimensionTypes.THE_NETHER, new HashMap<>());
        detectorBlockPosMap.put(DimensionTypes.THE_END, new HashMap<>());
    }

    public static void initPosMap(MinecraftServer server, ServerWorld world)
    {
        initMagneticMap();
        initDetectorMap();
    }

    public static RegistryKey<DimensionType> getDimensionKey(World world)
    {
        return world.getDimensionEntry().getKey().orElse(null);
    }

    public static BlockField detectorPlacementCheck(World world, BlockPos detectorPos)
    {
        ElectroMod.LOGGER.info("detectorPlacementCheck");
        BlockField passedPoses = new BlockField();
        MagneticField field;

        for (BlockPos magneticPos : magneticBlockPosMap.get(getDimensionKey(world)).keySet())
        {
            field = applyConditions(world, detectorPos, magneticPos);
            if (field == null) {
                ElectroMod.LOGGER.info("not met placement: {}", magneticPos); continue; }

            passedPoses.putField(magneticPos, field);
        }

        return passedPoses;
    }

    public static void updateValidPos(World world, BlockPos updatedMagneticPos)
    {
        var dimensionKey = getDimensionKey(world);
        MagneticField field;

        for (BlockPos detectorPos : detectorBlockPosMap.get(dimensionKey).keySet())
        {
            field = applyConditions(world, detectorPos, updatedMagneticPos);
            if (field == null) { ElectroMod.LOGGER.info("not met update"); continue; }

            detectorBlockPosMap.get(dimensionKey).get(detectorPos).putField(updatedMagneticPos, field);    // add + edit
            UpdateWatchCallBack.UPDATED.invoker().Broadcast(detectorPos, updatedMagneticPos);
        }
    }

    public static void removeValidPos(World world, BlockPos removedMagneticPos)
    {
        var dimensionKey = getDimensionKey(world);

        for (BlockPos detectorPos : detectorBlockPosMap.get(dimensionKey).keySet())
        {
            detectorBlockPosMap.get(dimensionKey).get(detectorPos).removeField(removedMagneticPos); // method has condition to check existance
            UpdateWatchCallBack.REMOVED.invoker().Broadcast(detectorPos, removedMagneticPos);
        }
    }

    @Nullable
    public static MagneticField applyConditions(World world, BlockPos detectorPos, BlockPos magneticPos)
    {
        ElectroMod.LOGGER.info("applyCondition");

        if (!RelativeDistanceCondition(detectorPos, magneticPos)) return null;

        MagneticField field = FindMagneticField(world, detectorPos, magneticPos);
        if (field == null) { ElectroMod.LOGGER.info("vec not found"); return null; } // is there field?

        if (field.getMagneticPower() <= 0)  // is field force positive
        { ElectroMod.LOGGER.info("field force under 0"); return null; }

        return field;
    }

    public static boolean RelativeDistanceCondition(BlockPos detectorPos, BlockPos magneticPos)
    {
        return Math.abs(detectorPos.getX() - magneticPos.getX()) <= ForceProfile.minmumDistance &&
                Math.abs(detectorPos.getY() - magneticPos.getY()) <= ForceProfile.minmumDistance &&
                Math.abs(detectorPos.getZ() - magneticPos.getZ()) <= ForceProfile.minmumDistance;
    }

    @Nullable
    public static MagneticField FindMagneticField(World world, BlockPos detectorPos, BlockPos magneticPos)
    {
        ElectroMod.LOGGER.info("FindMVec start");

        ForceProfile profile = getForceProfile(getField(world, magneticPos));

        var foundMVec = profile.find(magneticPos, detectorPos);

        return foundMVec == null ? null :
               foundMVec.toMagneticField(getField(world, magneticPos).getMagneticPower());
    }

    public static ForceProfile getForceProfile(MagneticField field)
    {
        ElectroMod.LOGGER.info("getForceProfile");
        ElectroMod.LOGGER.info(field.getForceDirection().toString());

        return ForceProfile.createForceProfile(getPowerCategory(field.getMagneticPower()), field.getForceDirection());
    }

    @Nullable
    public static ForceProfile.powerCategory getPowerCategory(int magneticPower)
    {
        ElectroMod.LOGGER.info("powercat inp: {}", magneticPower);

        if (magneticPower > CopperCoilBlock.copperAdditiveFactor) return ForceProfile.powerCategory.COPPER;
        else if (magneticPower > GoldenCoilBlock.goldAdditiveFactor) return ForceProfile.powerCategory.GOLD;
        else if (magneticPower > IronCoilBlock.ironAdditiveFactor) return ForceProfile.powerCategory.IRON;

        return null;    // magneticPower = 0
    }

    public static Set<BlockPos> testPlacementCheck(World world, BlockPos pos)
    {
        return detectorBlockPosMap.get(getDimensionKey(world)).get(pos).getFields().keySet();
    }
}
