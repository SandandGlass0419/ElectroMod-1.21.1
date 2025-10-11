package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.ElectroMod;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public record ForceProfile(Vector<Set<MVec3i>> headProfile, Vector<Set<MVec3i>> bodyProfile, Vector<Set<MVec3i>> tailProfile)
{
    public static final Vector<Set<MVec3i>> EMPTY = createEmpty();

    private static final Vector<Set<MVec3i>> POWER0_HEAD_NORTH = createPower0HeadNorth();
    private static final Vector<Set<MVec3i>> POWER0_BODY_NORTH = createPower0BodyNorth();
    private static final Vector<Set<MVec3i>> POWER0_TAIL_NORTH = createPower0TailNorth();

    private static final Vector<Set<MVec3i>> POWER1_HEAD_NORTH = createPower1HeadNorth();
    private static final Vector<Set<MVec3i>> POWER1_BODY_NORTH = createPower1BodyNorth();
    private static final Vector<Set<MVec3i>> POWER1_TAIL_NORTH = createPower1TailNorth();

    private static final Vector<Set<MVec3i>> POWER2_HEAD_NORTH = createPower2HeadNorth();
    private static final Vector<Set<MVec3i>> POWER2_BODY_NORTH = createPower2BodyNorth();
    private static final Vector<Set<MVec3i>> POWER2_TAIL_NORTH = createPower2TailNorth();

    public static void registerForceProfiles()
    {
        ElectroMod.LOGGER.info("Registering ForceProfile");
    }

    public static int getPowerCategory(int magneticPower)
    {
        if (magneticPower < 0 || magneticPower > 15) return -1;

        return magneticPower > 2 ? 2 : magneticPower - 1;   // 0 returns -1 -> gets EMPTY
    }

    public static ForceProfile getForceProfile(int headPowerCategory, int bodyPowerCategory, int tailPowerCategory, Direction forceDirection)
    {
        return new ForceProfile(
                getHead(headPowerCategory, forceDirection),
                getBody(bodyPowerCategory, forceDirection),
                getTail(tailPowerCategory, forceDirection)
        );
    }

    public static ForceProfile getForceProfile(int powerCategory, Direction forceDirection)
    {
        return new ForceProfile(
                getHead(powerCategory, forceDirection),
                getBody(powerCategory, forceDirection),
                getTail(powerCategory, forceDirection)
        );
    }

    public static Vector<Set<MVec3i>> getHead(int powerCategory, Direction forceDirection)
    {
        return switch (powerCategory)
        {
            case 0 -> rotateNorthProfile(POWER0_HEAD_NORTH, forceDirection);
            case 1 -> rotateNorthProfile(POWER1_HEAD_NORTH, forceDirection);
            case 2 -> rotateNorthProfile(POWER2_HEAD_NORTH, forceDirection);
            default -> EMPTY;
        };
    }

    public static Vector<Set<MVec3i>> getBody(int powerCategory, Direction forceDirection)
    {
        return switch (powerCategory)
        {
            case 0 -> rotateNorthProfile(POWER0_BODY_NORTH, forceDirection);
            case 1 -> rotateNorthProfile(POWER1_BODY_NORTH, forceDirection);
            case 2 -> rotateNorthProfile(POWER2_BODY_NORTH, forceDirection);
            default -> EMPTY;
        };
    }

    public static Vector<Set<MVec3i>> getTail(int powerCategory, Direction forceDirection)
    {
        return switch (powerCategory)
        {
            case 0 -> rotateNorthProfile(POWER0_TAIL_NORTH, forceDirection);
            case 1 -> rotateNorthProfile(POWER1_TAIL_NORTH, forceDirection);
            case 2 -> rotateNorthProfile(POWER2_TAIL_NORTH, forceDirection);
            default -> EMPTY;
        };
    }

    private static Vector<Set<MVec3i>> createEmpty()
    {                                     // power 0       // power 1       // power 2
        return new Vector<>(Arrays.asList(new HashSet<>(), new HashSet<>(), new HashSet<>()));
    }

    private static Set<MVec3i> getPower0Core()
    {
        return Set.of(
                new MVec3i(0, 0, -1, Direction.NORTH),  // core (north)
                new MVec3i(0, 0, 1, Direction.NORTH)   // core (south)
        );
    }

    private static Vector<Set<MVec3i>> createPower0HeadNorth()
    {
        Set<MVec3i> power0Element = new HashSet<>(Set.of(
                new MVec3i(0, 1, -1, Direction.UP),     // up
                new MVec3i(0, -1, -1, Direction.DOWN),  // down
                new MVec3i(1, 0, -1, Direction.EAST),   // right
                new MVec3i(-1, 0, -1, Direction.WEST)   // left
        ));
        power0Element.addAll(getPower0Core());

        Vector<Set<MVec3i>> northProfile = createEmpty();
        northProfile.set(0, power0Element);

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createPower0BodyNorth()
    {
        Set<MVec3i> power0Element = new HashSet<>(Set.of(
                new MVec3i(0, 1, 0, Direction.SOUTH),   // up
                new MVec3i(0, -1, 0, Direction.SOUTH),  // down
                new MVec3i(1, 0, 0, Direction.SOUTH),   // right
                new MVec3i(-1, 0, 0, Direction.SOUTH)   // left
        ));
        power0Element.addAll(getPower0Core());

        Vector<Set<MVec3i>> northProfile = createEmpty();
        northProfile.set(0, power0Element);

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createPower0TailNorth()
    {
        Set<MVec3i> power0Element = new HashSet<>(Set.of(
                new MVec3i(0, 1, 1, Direction.DOWN),    // up
                new MVec3i(0, -1, 1, Direction.UP),     // down
                new MVec3i(1, 0, 1, Direction.WEST),    // right
                new MVec3i(-1, 0, 1, Direction.EAST)    // left
        ));
        power0Element.addAll(getPower0Core());

        Vector<Set<MVec3i>> northProfile = createEmpty();
        northProfile.set(0, power0Element);

        return northProfile;
    }

    private static Set<MVec3i> getPower1Core()
    {
        return Set.of(
                new MVec3i(0, 0, -2, Direction.NORTH),  // core2 (north)
                new MVec3i(0, 0, 2, Direction.NORTH)   // core2 (south)
        );
    }

    private static Vector<Set<MVec3i>> createPower1HeadNorth()
    {
        Set<MVec3i> power0Element = Set.of(        // dont need core
                new MVec3i(-1, 1, -1, Direction.SOUTH), // up left
                new MVec3i(1, 1, -1, Direction.SOUTH),  // up right
                new MVec3i(-1, -1, -1, Direction.SOUTH),// down left
                new MVec3i(1, -1, -1, Direction.SOUTH)  // down right
        );

        Vector<Set<MVec3i>> northProfile = createPower0HeadNorth();
        northProfile.get(0).addAll(power0Element);     // modify power 0

        Set<MVec3i> power1ElementElement = Set.of( // up facing north
                new MVec3i(0, 1, -2, Direction.UP),
                new MVec3i(0, 2, -2, Direction.UP),
                new MVec3i(0, 2, -1, Direction.SOUTH)
        );

        Set<MVec3i> power1Element = createNorthElement(power1ElementElement,
                MVec3i.Angles.CLOCK_360,
                MVec3i.Angles.CLOCK_90,
                MVec3i.Angles.CLOCK_180,
                MVec3i.Angles.CLOCK_270
        );
        power1Element.addAll(getPower0Core());
        power1Element.addAll(getPower1Core());

        northProfile.set(1, power1Element);

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createPower1BodyNorth()
    {
        Set<MVec3i> power0Element = Set.of(        // don't need core
                new MVec3i(-1, 1, 0, Direction.SOUTH),  // up left
                new MVec3i(1, 1, 0, Direction.SOUTH),   // up right
                new MVec3i(-1, -1, 0, Direction.SOUTH), // left down
                new MVec3i(1, -1, 0, Direction.SOUTH)   // left right
        );

        Vector<Set<MVec3i>> northProfile = createPower0BodyNorth();
        northProfile.get(0).addAll(power0Element);     // modify power 0

        Set<MVec3i> power1Element = new HashSet<>(Set.of(        // don't need rotation
                new MVec3i(0, 2, 0, Direction.SOUTH),   // up
                new MVec3i(0, -2, 0, Direction.SOUTH),  // down
                new MVec3i(2, 0, 0, Direction.SOUTH),   // right
                new MVec3i(-2, 0, 0, Direction.SOUTH)   // left
        ));
        power1Element.addAll(getPower0Core());
        power1Element.addAll(getPower1Core());

        northProfile.set(1, power1Element);

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createPower1TailNorth()
    {
        Set<MVec3i> power0Element = Set.of(       // dont need core
                new MVec3i(-1, 1, 1, Direction.SOUTH), // up left
                new MVec3i(1, 1, 1, Direction.SOUTH),  // up right
                new MVec3i(-1, -1, 1, Direction.SOUTH),// down left
                new MVec3i(1, -1, 1, Direction.SOUTH)  // down right
        );

        Vector<Set<MVec3i>> northProfile = createPower0TailNorth();
        northProfile.get(0).addAll(power0Element);     // modify power 0

        Set<MVec3i> power1ElementElement = Set.of( // up facing north
                new MVec3i(0, 1, 2, Direction.DOWN),
                new MVec3i(0, 2, 2, Direction.DOWN),
                new MVec3i(0, 2, 1, Direction.SOUTH)
        );

        Set<MVec3i> power1Element = createNorthElement(power1ElementElement,
                MVec3i.Angles.CLOCK_360,
                MVec3i.Angles.CLOCK_90,
                MVec3i.Angles.CLOCK_180,
                MVec3i.Angles.CLOCK_270
        );
        power1Element.addAll(getPower0Core());
        power1Element.addAll(getPower1Core());

        northProfile.set(1, power1Element);

        return northProfile;
    }

    private static Set<MVec3i> getPower2Core()
    {
        return Set.of(
                new MVec3i(0, 0, -3, Direction.NORTH),  // north
                new MVec3i(0, 0, 3, Direction.NORTH)    // south
        );
    }

    private static Vector<Set<MVec3i>> createPower2HeadNorth()
    {
        Set<MVec3i> power1ElementElement1 = Set.of(     // don't need core, curved element
                new MVec3i(-2, 1, -1, Direction.SOUTH), // left
                new MVec3i(-2, 1, -2, Direction.WEST),  // left
                new MVec3i(-1, 1, -2, Direction.WEST),  // left
                new MVec3i(2, 1, -1, Direction.SOUTH),  // right
                new MVec3i(2, 1, -2, Direction.EAST),   // right
                new MVec3i(1, 1, -2, Direction.EAST)    // right
        );
        Set<MVec3i> power1Element1 = createNorthElement(power1ElementElement1,
                new Vec3i(0, 0, 0), // up
                new Vec3i(0, -2, 0) // down
        );

        Set<MVec3i> power1ElementElement2 = Set.of(     // don't need core, straight element
                new MVec3i(-1, 2, -1, Direction.SOUTH),
                new MVec3i(-1, 2, -2, Direction.SOUTH)
        );
        Set<MVec3i> power1Element2 = createNorthElement(power1ElementElement2,
                new Vec3i(0, 0, 0),     // up left
                new Vec3i(2, 0, 0),     // up right
                new Vec3i(0, -4, 0),    // down left
                new Vec3i(2, -4, 0)     // down right
        );

        Vector<Set<MVec3i>> northProfile = createPower1HeadNorth();
        northProfile.get(1).addAll(power1Element1);  // modify power 1
        northProfile.get(1).addAll(power1Element2);

        Set<MVec3i> power2ElementElement = Set.of(    // up facing north
                new MVec3i(0, 1, -3, Direction.UP),
                new MVec3i(0, 2, -3, Direction.UP),
                new MVec3i(0, 3, -3, Direction.UP),
                new MVec3i(0, 3, -2, Direction.SOUTH),
                new MVec3i(0, 3, -1, Direction.SOUTH)
        );
        Set<MVec3i> power2Element = createNorthElement(power2ElementElement,
                MVec3i.Angles.CLOCK_360,
                MVec3i.Angles.CLOCK_90,
                MVec3i.Angles.CLOCK_180,
                MVec3i.Angles.CLOCK_270
        );
        power2Element.addAll(getPower0Core());
        power2Element.addAll(getPower1Core());
        power2Element.addAll(getPower2Core());

        northProfile.set(2, power2Element);

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createPower2BodyNorth()
    {
        Set<MVec3i> power1Element = Set.of(             // don't need core
                new MVec3i(-1, 2, 0, Direction.SOUTH),  // up left up
                new MVec3i(-2, 1, 0, Direction.SOUTH),  // up left left
                new MVec3i(1, 2, 0, Direction.SOUTH),   // up right up
                new MVec3i(2, 1, 0, Direction.SOUTH),   // up right right
                new MVec3i(-1, -2, 0, Direction.SOUTH), // down left down
                new MVec3i(-2, -1, 0, Direction.SOUTH), // down left left
                new MVec3i(1, -2, 0, Direction.SOUTH),  // down right down
                new MVec3i(2, -1, 0, Direction.SOUTH)   // down right right
        );

        Vector<Set<MVec3i>> northProfile = createPower1BodyNorth();
        northProfile.get(1).addAll(power1Element);  // modify power 1

        Set<MVec3i> power2Element = new HashSet<>(Set.of(   // don't need rotation
                new MVec3i(0, 3, 0, Direction.SOUTH),   // up
                new MVec3i(0, -3, 0, Direction.SOUTH),  // down
                new MVec3i(-3, 0, 0, Direction.SOUTH),  // left
                new MVec3i(3, 0, 0, Direction.SOUTH)    // right
        ));
        power2Element.addAll(getPower0Core());
        power2Element.addAll(getPower1Core());
        power2Element.addAll(getPower2Core());

        northProfile.set(2, power2Element);

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createPower2TailNorth()
    {
        Set<MVec3i> power1ElementElement1 = Set.of(     // don't need core, curved element
                new MVec3i(-2, 1, 1, Direction.SOUTH),  // left
                new MVec3i(-2, 1, 2, Direction.EAST),   // left
                new MVec3i(-1, 1, 2, Direction.EAST),   // left
                new MVec3i(2, 1, 1, Direction.SOUTH),   // right
                new MVec3i(2, 1, 2, Direction.WEST),    // right
                new MVec3i(1, 1, 2, Direction.WEST)     // right
        );
        Set<MVec3i> power1Element1 = createNorthElement(power1ElementElement1,
                new Vec3i(0, 0, 0), // up
                new Vec3i(0, -2, 0) // down
        );

        Set<MVec3i> power1ElementElement2 = Set.of(     // don't need core, straight element
                new MVec3i(-1, 2, 1, Direction.SOUTH),
                new MVec3i(-1, 2, 2, Direction.SOUTH)
        );
        Set<MVec3i> power1Element2 = createNorthElement(power1ElementElement2,
                new Vec3i(0, 0, 0),     // up left
                new Vec3i(2, 0, 0),     // up right
                new Vec3i(0, -4, 0),    // down left
                new Vec3i(2, -4, 0)     // down right
        );

        Vector<Set<MVec3i>> northProfile = createPower1TailNorth();
        northProfile.get(1).addAll(power1Element1);
        northProfile.get(1).addAll(power1Element2);

        Set<MVec3i> power2ElementElement = Set.of(      // up facing north
                new MVec3i(0, 1, 3, Direction.DOWN),
                new MVec3i(0, 2, 3, Direction.DOWN),
                new MVec3i(0, 3, 3, Direction.DOWN),
                new MVec3i(0, 3, 2, Direction.SOUTH),
                new MVec3i(0, 3, 1, Direction.SOUTH)
        );
        Set<MVec3i> power2Element = createNorthElement(power2ElementElement,
                MVec3i.Angles.CLOCK_360,
                MVec3i.Angles.CLOCK_90,
                MVec3i.Angles.CLOCK_180,
                MVec3i.Angles.CLOCK_270
        );
        power2Element.addAll(getPower0Core());
        power2Element.addAll(getPower1Core());
        power2Element.addAll(getPower2Core());

        northProfile.set(2, power2Element);

        return northProfile;
    }

    private static Set<MVec3i> createNorthElement(Set<MVec3i> northElementElement, Vec3i... variations)
    {
        Set<MVec3i> northElement = new HashSet<>();

        for (Vec3i variation : variations)
        {
            northElement.addAll(MVec3i.add(northElementElement, variation));
        }

        return northElement;
    }

    private static Set<MVec3i> createNorthElement(Set<MVec3i> northElementElement, MVec3i.Angles... angles)
    {
        Set<MVec3i> northElement = new HashSet<>();

        Direction.Axis axis = Direction.Axis.Z;
        int angleOrdinal;

        for (MVec3i.Angles angle : angles)
        {
            angleOrdinal = angle.ordinal();
            northElement.addAll(MVec3i.rotate90(northElementElement, axis, angleOrdinal));
        }

        return northElement;
    }

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
