package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.Set;
import java.util.Vector;

public record ForceProfile(Set<MVec3i> Core1, Set<MVec3i>[] Core1Profile1, Set<MVec3i>[] Core1Profile2,
                           Set<MVec3i> Core2, Set<MVec3i>[] Core2Profile1, Set<MVec3i>[] Core2Profile2, Set<MVec3i>[] Core2Profile3,
                           Set<MVec3i> Core3, Set<MVec3i>[] Core3Profile1)
{

    private static Set<MVec3i> createCore(int core, Direction forceDirection)
    {
        Set<MVec3i> northCore = Set.of(new MVec3i(0, 0, core, Direction.NORTH),
                                       new MVec3i(0, 0, -1*core, Direction.NORTH));

        int angleOrdinal = rotationAngleTable(forceDirection).ordinal();
        Direction.Axis axis = rotationAxisTable(forceDirection);

        return MVec3i.rotate90(northCore, axis, angleOrdinal);
    }

    private static Vector<Set<MVec3i>> createNorthProfiles(Set<MVec3i> defaultElement, Vec3i... variations)
    {
        Vector<Set<MVec3i>> northProfile = new Vector<>();

        for (Vec3i variation : variations)
        {
            northProfile.add(MVec3i.add(defaultElement, variation));
        }

        return northProfile;
    }

    // core1 profiles
    private static Vector<Set<MVec3i>> createCore1Profile1(Direction forceDirection)
    {
        Set<MVec3i> defaultElement = Set.of( // up heading north
                new MVec3i(0, 1, -1, Direction.UP),
                new MVec3i(0, 1, 0, Direction.SOUTH),
                new MVec3i(0, 1, 1, Direction.DOWN));

        Vector<Set<MVec3i>> northProfile = createNorthProfiles(defaultElement,
                new Vec3i(0, 0, 0),     // up
                new Vec3i(1, -1, 0),    // right
                new Vec3i(0, -2, 0),    // down
                new Vec3i(-1, -1, 0));  // left

        return rotateNorthProfile(northProfile, forceDirection);
    }

    private static Vector<Set<MVec3i>> createCore1Profile2(Direction forceDirection)
    {
        Set<MVec3i> defaultElement = Set.of( // up left heading north
                new MVec3i(-1, 1, -1, Direction.SOUTH),
                new MVec3i(-1, 1, 0, Direction.SOUTH),
                new MVec3i(-1, 1, 1, Direction.SOUTH));

        Vector<Set<MVec3i>> northProfile = createNorthProfiles(defaultElement,
                new Vec3i(0, 0, 0),     // up left
                new Vec3i(2, 0, 0),     // up right
                new Vec3i(2, -2, 0),    // down right
                new Vec3i(0, -2, 0)     // down left
        );

        return rotateNorthProfile(northProfile, forceDirection);
    }

    // core2 profiles
//    private static Set<MVec3i>[] createCore2Profile1(Direction forceDirection)
//    {
//
//    }
//
//    private static Set<MVec3i>[] createCore2Profile2(Direction forceDirection)
//    {
//
//    }
//
//    private static Set<MVec3i>[] createCore2Profile3(Direction forceDirection)
//    {
//
//    }
//
//    // core3 profile
//    private static Set<MVec3i>[] createCore3Profile1(Direction forceDirection)
//    {
//
//    }

    private static Vector<Set<MVec3i>> rotateNorthProfile(Vector<Set<MVec3i>> northProfile, Direction forceDirection)
    {
        Vector<Set<MVec3i>> Profile = new Vector<>();

        Direction.Axis axis;
        int angleOrdinal;

        for (Set<MVec3i> profileElement : northProfile)
        {
            axis = rotationAxisTable(forceDirection);
            angleOrdinal = rotationAngleTable(forceDirection).ordinal();

            Profile.add(MVec3i.rotate90(profileElement, axis, angleOrdinal));
        }

        return Profile;
    }

    private static Direction.Axis rotationAxisTable(Direction resultDirection)
    {
        // start from north!
        return switch (resultDirection)
        {
            case UP, DOWN -> Direction.Axis.X;
            case NORTH, SOUTH, EAST, WEST -> Direction.Axis.Y;
        };
    }

    private static MVec3i.Angles rotationAngleTable(Direction resultDirection)
    {
        // start from north!
        return switch (resultDirection)
        {
            case UP -> MVec3i.Angles.CLOCK_270;
            case DOWN -> MVec3i.Angles.CLOCK_90;
            case NORTH -> MVec3i.Angles.CLOCK_360;
            case SOUTH -> MVec3i.Angles.CLOCK_180;
            case EAST -> MVec3i.Angles.CLOCK_90;
            case WEST -> MVec3i.Angles.CLOCK_270;
        };
    }

}
