package net.devs.electromod;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.custom.magnetic.MagneticForce.ForceProfile;
import net.devs.electromod.block.custom.magnetic.MagneticForce.MVec3i;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.components.ModDataComponentTypes;
import net.devs.electromod.item.ModItemGroups;
import net.devs.electromod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ElectroMod implements ModInitializer
{
    public static final String MOD_ID = "electromod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
    {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerModItemGroups();
        ModDataComponentTypes.registerModDataComponentTypes();
        ModBlockEntities.registerModBlockEntities();

        ForceProfile.registerForceProfiles();
        //Test();
	}

    private static void Test()
    {
       //testRotation();
       testSetRotation();
    }

    private static void testRotation()
    {
        MVec3i mVec3i = new MVec3i(1, -1, 2, Direction.UP);
        var res = mVec3i.rotate90(Direction.Axis.Z, MVec3i.Angles.CLOCK_270.ordinal());
        ElectroMod.LOGGER.info("result: " + res.getX() + res.getY() + res.getZ() + res.getForceDirection().toString());
    }

    private static void testSetRotation()
    {
        Set<MVec3i> defaultElementleft = Set.of( // up left heading north
                new MVec3i(-1, 1, -2, Direction.WEST),
                new MVec3i(-2, 1, -2, Direction.WEST),
                new MVec3i(-2, 1, -1, Direction.SOUTH),
                new MVec3i(-2, 1, 0, Direction.SOUTH),
                new MVec3i(-2, 1, 1, Direction.SOUTH),
                new MVec3i(-2, 1, 2, Direction.EAST),
                new MVec3i(-1, 1, 2, Direction.EAST)
        );

        var res = MVec3i.rotate90(defaultElementleft, Direction.Axis.Z, MVec3i.Angles.CLOCK_180.ordinal());

        for (MVec3i vec : res)
        {
            ElectroMod.LOGGER.info("result: " + vec.getX() + vec.getY() + vec.getZ() + vec.getForceDirection().toString());
        }
    }
}