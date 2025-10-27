package net.devs.electromod.block.custom.magnetic.force;

import net.devs.electromod.ElectroMod;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public record ForceProfile(Vector<Set<MVec3i>> headProfile, Vector<Set<MVec3i>> bodyProfile, Vector<Set<MVec3i>> tailProfile)
{
    private static final Vector<Set<MVec3i>> EMPTY = createEmpty();

    private static final Vector<Set<MVec3i>> IRON_HEAD_NORTH = createIronHeadNorth();
    private static final Vector<Set<MVec3i>> IRON_BODY_NORTH = createIronBodyNorth();
    private static final Vector<Set<MVec3i>> IRON_TAIL_NORTH = createIronTailNorth();

    private static final Vector<Set<MVec3i>> GOLD_HEAD_NORTH = createGoldHeadNorth();
    private static final Vector<Set<MVec3i>> GOLD_BODY_NORTH = createGoldBodyNorth();
    private static final Vector<Set<MVec3i>> GOLD_TAIL_NORTH = createGoldTailNorth();

    private static final Vector<Set<MVec3i>> COPPER_HEAD_NORTH = createCopperHeadNorth();
    private static final Vector<Set<MVec3i>> COPPER_BODY_NORTH = createCopperBodyNorth();
    private static final Vector<Set<MVec3i>> COPPER_TAIL_NORTH = createCopperTailNorth();

    private static final Vector<Set<MVec3i>> MAGNET_HEAD_NORTH = createMagnetHeadNorth();
    private static final Vector<Set<MVec3i>> MAGNET_BODY_NORTH = createMagnetBodyNorth();
    private static final Vector<Set<MVec3i>> MAGNET_TAIL_NORTH = createMagnetTailNorth();

    public static final int minmumDistance = 15;

    public static void registerForceProfiles()
    {
        ElectroMod.LOGGER.info("Registering ForceProfiles");
    }

    public static Set<BlockPos> deployPos(BlockPos pos, Set<MVec3i> deltas)
    {
        return MVec3i.add(deltas, pos);
    }

    public static Set<BlockPos> deployPos(BlockPos pos, ForceProfile profile)
    {
        Set<BlockPos> positions = new HashSet<>();

        for (var deltas : profile.headProfile())
        { positions.addAll(deployPos(pos, deltas)); }

        for (var deltas : profile.bodyProfile())
        { positions.addAll(deployPos(pos, deltas)); }

        for (var deltas : profile.tailProfile())
        { positions.addAll(deployPos(pos, deltas)); }

        return positions;
    }

    @Nullable
    public Integer getIndex(BlockPos magneticPos, BlockPos targetPos)
    {
        int index = 0;

        for (var deltas : this.headProfile())
        {
            if (deployPos(magneticPos, deltas).contains(targetPos)) return index;
            index++;
        }

        index = 0;
        for (var deltas : this.bodyProfile())
        {
            if (deployPos(magneticPos, deltas).contains(targetPos)) return index;
            index++;
        }

        index = 0;
        for (var deltas : this.tailProfile())
        {
            if (deployPos(magneticPos, deltas).contains(targetPos)) return index;
            index++;
        }

        return null;
    }

    public Set<BlockPos> getWatch(int index, BlockPos magneticPos)
    {
        Set<BlockPos> watch = new HashSet<>();

        if (this.headProfile().size() > index)
        { watch.addAll(MVec3i.add(this.headProfile().get(index), magneticPos)); }

        if (this.bodyProfile().size() > index)
        { watch.addAll(MVec3i.add(this.bodyProfile().get(index), magneticPos)); }

        if (this.tailProfile().size() > index)
        { watch.addAll(MVec3i.add(this.tailProfile().get(index), magneticPos)); }

        watch.addAll(MVec3i.add(this.headProfile().getFirst(), magneticPos));
        return watch;
    }

    @Nullable
    public MVec3i find(final BlockPos magneticPos, BlockPos targetPos)
    {
        for (var deltas : this.headProfile)
        {
            for (var mvec : deltas)
            {
                 if (magneticPos.add(mvec).equals(targetPos)) return mvec;
            }
        }

        for (var deltas : this.bodyProfile)
        {
            for (var mvec : deltas)
            {
                if (magneticPos.add(mvec).equals(targetPos)) return mvec;
            }
        }

        for (var deltas : this.tailProfile)
        {
            for (var mvec : deltas)
            {
                if (magneticPos.add(mvec).equals(targetPos)) return mvec;
            }
        }

        return null;
    }

    public static ForceProfile getEmptyProfile()
    {
        return new ForceProfile(EMPTY, EMPTY, EMPTY);
    }

    public static ForceProfile createForceProfile(powerCategory headPowerCategory, powerCategory bodyPowerCategory, powerCategory tailPowerCategory, Direction forceDirection)
    {
        return new ForceProfile(
                getHead(headPowerCategory, forceDirection),
                getBody(bodyPowerCategory, forceDirection),
                getTail(tailPowerCategory, forceDirection)
        );
    }

    public static ForceProfile createForceProfile(powerCategory powerCategory, Direction forceDirection)
    {
        return new ForceProfile(
                getHead(powerCategory, forceDirection),
                getBody(powerCategory, forceDirection),
                getTail(powerCategory, forceDirection)
        );
    }

    public static Vector<Set<MVec3i>> getHead(powerCategory powerCategory, Direction forceDirection)
    {
        return switch (powerCategory)
        {
            case IRON -> rotateNorthProfile(IRON_HEAD_NORTH, forceDirection);
            case GOLD -> rotateNorthProfile(GOLD_HEAD_NORTH, forceDirection);
            case COPPER -> rotateNorthProfile(COPPER_HEAD_NORTH, forceDirection);
            case MAGNET -> rotateNorthProfile(MAGNET_HEAD_NORTH, forceDirection);
            default -> EMPTY;
        };
    }

    public static Vector<Set<MVec3i>> getBody(powerCategory powerCategory, Direction forceDirection)
    {
        return switch (powerCategory)
        {
            case IRON -> rotateNorthProfile(IRON_BODY_NORTH, forceDirection);
            case GOLD -> rotateNorthProfile(GOLD_BODY_NORTH, forceDirection);
            case COPPER -> rotateNorthProfile(COPPER_BODY_NORTH, forceDirection);
            case MAGNET -> rotateNorthProfile(MAGNET_BODY_NORTH, forceDirection);
            default -> EMPTY;
        };
    }

    public static Vector<Set<MVec3i>> getTail(powerCategory powerCategory, Direction forceDirection)
    {
        return switch (powerCategory)
        {
            case IRON -> rotateNorthProfile(IRON_TAIL_NORTH, forceDirection);
            case GOLD -> rotateNorthProfile(GOLD_TAIL_NORTH, forceDirection);
            case COPPER -> rotateNorthProfile(COPPER_TAIL_NORTH, forceDirection);
            case MAGNET -> rotateNorthProfile(MAGNET_TAIL_NORTH, forceDirection);
            default -> EMPTY;
        };
    }

    private static Vector<Set<MVec3i>> createEmpty()
    {                               // Core
        return new Vector<>(List.of(new HashSet<>()));
    }

    private static Set<MVec3i> getIronCore()
    {
        return Set.of(
                new MVec3i(0, 0, -1, Direction.NORTH, 0),  // core (north)
                new MVec3i(0, 0, 1, Direction.NORTH, 0)   // core (south)
        );
    }

    private static Vector<Set<MVec3i>> createIronHeadNorth()
    {
        Vector<Set<MVec3i>> northProfile = createEmpty();

        northProfile.getFirst().addAll(getIronCore());  // core

        northProfile.add(Set.of(new MVec3i(0, 1, -1, Direction.UP, powerCategory.IRON.get())));         // up
        northProfile.add((Set.of(new MVec3i(0, -1, -1, Direction.DOWN, powerCategory.IRON.get()))));    // down
        northProfile.add(Set.of(new MVec3i(-1, 0, -1, Direction.WEST, powerCategory.IRON.get())));      // left
        northProfile.add(Set.of(new MVec3i(1, 0, -1, Direction.EAST, powerCategory.IRON.get())));       // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createIronBodyNorth()
    {
        Vector<Set<MVec3i>> northProfile = createEmpty();

        northProfile.getFirst().addAll(getIronCore());  // core

        northProfile.add(Set.of(new MVec3i(0, 1, 0, Direction.SOUTH, powerCategory.IRON.get())));   // up
        northProfile.add(Set.of(new MVec3i(0, -1, 0, Direction.SOUTH, powerCategory.IRON.get())));  // down
        northProfile.add(Set.of(new MVec3i(-1, 0, 0, Direction.SOUTH, powerCategory.IRON.get())));   // left
        northProfile.add(Set.of(new MVec3i(1, 0, 0, Direction.SOUTH, powerCategory.IRON.get())));  // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createIronTailNorth()
    {
        Vector<Set<MVec3i>> northProfile = createEmpty();

        northProfile.getFirst().addAll(getIronCore());  // core

        northProfile.add(Set.of(new MVec3i(0, 1, 1, Direction.DOWN, powerCategory.IRON.get())));    // up
        northProfile.add(Set.of(new MVec3i(0, -1, 1, Direction.UP, powerCategory.IRON.get())));     // down
        northProfile.add(Set.of(new MVec3i(-1, 0, 1, Direction.EAST, powerCategory.IRON.get())));   // left
        northProfile.add(Set.of(new MVec3i(1, 0, 1, Direction.WEST, powerCategory.IRON.get())));    // right

        return northProfile;
    }

    private static Set<MVec3i> getGoldCore()
    {
        return Set.of(
                new MVec3i(0, 0, -2, Direction.NORTH, powerCategory.GOLD.get()),  // core2 (north)
                new MVec3i(0, 0, 2, Direction.NORTH, powerCategory.GOLD.get())   // core2 (south)
        );
    }

    private static Vector<Set<MVec3i>> createGoldHeadNorth()
    {
        Vector<Set<MVec3i>> northProfile = createIronHeadNorth();

        northProfile.getFirst().addAll(getGoldCore());  // core

        northProfile.add(Set.of(new MVec3i(-1, 1, -1, Direction.SOUTH, powerCategory.IRON.get())));     // up left
        northProfile.add(Set.of(new MVec3i(1, 1, -1, Direction.SOUTH, powerCategory.IRON.get())));      // up right
        northProfile.add(Set.of(new MVec3i(-1, -1, -1, Direction.SOUTH, powerCategory.IRON.get())));    // down left
        northProfile.add(Set.of(new MVec3i(1, -1, -1, Direction.SOUTH, powerCategory.IRON.get())));     // down right


        Set<MVec3i> goldHeadElement = Set.of( // up facing north
                new MVec3i(0, 1, -2, Direction.UP, powerCategory.GOLD.get()),
                new MVec3i(0, 2, -2, Direction.UP, powerCategory.GOLD.get()),
                new MVec3i(0, 2, -1, Direction.SOUTH, powerCategory.GOLD.get())
        );

        northProfile.add(createNorthElement(goldHeadElement, MVec3i.Angles.CLOCK_360)); // up
        northProfile.add(createNorthElement(goldHeadElement, MVec3i.Angles.CLOCK_180)); // down
        northProfile.add(createNorthElement(goldHeadElement, MVec3i.Angles.CLOCK_270)); // left
        northProfile.add(createNorthElement(goldHeadElement, MVec3i.Angles.CLOCK_90));  // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createGoldBodyNorth()
    {
        Vector<Set<MVec3i>> northProfile = createIronBodyNorth();

        northProfile.getFirst().addAll(getGoldCore());      // core

        northProfile.add(Set.of(new MVec3i(-1, 1, 0, Direction.SOUTH, powerCategory.IRON.get())));  // up left
        northProfile.add(Set.of(new MVec3i(1, 1, 0, Direction.SOUTH, powerCategory.IRON.get())));   // up right
        northProfile.add(Set.of(new MVec3i(-1, -1, 0, Direction.SOUTH, powerCategory.IRON.get()))); // down left
        northProfile.add(Set.of(new MVec3i(1, -1, 0, Direction.SOUTH, powerCategory.IRON.get())));  // down right


        northProfile.add(Set.of(new MVec3i(0, 2, 0, Direction.SOUTH, powerCategory.GOLD.get())));   // up
        northProfile.add(Set.of(new MVec3i(0, -2, 0, Direction.SOUTH, powerCategory.GOLD.get())));  // down
        northProfile.add(Set.of(new MVec3i(-2, 0, 0, Direction.SOUTH, powerCategory.GOLD.get())));  // left
        northProfile.add(Set.of(new MVec3i(2, 0, 0, Direction.SOUTH, powerCategory.GOLD.get())));   // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createGoldTailNorth()
    {
        Vector<Set<MVec3i>> northProfile = createIronTailNorth();

        northProfile.getFirst().addAll(getGoldCore());      // core

        northProfile.add(Set.of(new MVec3i(-1, 1, 1, Direction.SOUTH, powerCategory.IRON.get())));  // up left
        northProfile.add(Set.of(new MVec3i(1, 1, 1, Direction.SOUTH, powerCategory.IRON.get())));   // up right
        northProfile.add(Set.of(new MVec3i(-1, -1, 1, Direction.SOUTH, powerCategory.IRON.get()))); // down left
        northProfile.add(Set.of(new MVec3i(1, -1, 1, Direction.SOUTH, powerCategory.IRON.get())));  // down right


        Set<MVec3i> goldTailElement = Set.of( // up facing north
                new MVec3i(0, 1, 2, Direction.DOWN, powerCategory.GOLD.get()),
                new MVec3i(0, 2, 2, Direction.DOWN, powerCategory.GOLD.get()),
                new MVec3i(0, 2, 1, Direction.SOUTH, powerCategory.GOLD.get())
        );

        northProfile.add(createNorthElement(goldTailElement, MVec3i.Angles.CLOCK_360)); // up
        northProfile.add(createNorthElement(goldTailElement, MVec3i.Angles.CLOCK_180)); // down
        northProfile.add(createNorthElement(goldTailElement, MVec3i.Angles.CLOCK_270)); // left
        northProfile.add(createNorthElement(goldTailElement, MVec3i.Angles.CLOCK_90));  // right

        return northProfile;
    }

    private static Set<MVec3i> getCopperCore()
    {
        return Set.of(
                new MVec3i(0, 0, -3, Direction.NORTH, powerCategory.COPPER.get()),  // north
                new MVec3i(0, 0, 3, Direction.NORTH ,powerCategory.COPPER.get())    // south
        );
    }

    private static Vector<Set<MVec3i>> createCopperHeadNorth()
    {
        Vector<Set<MVec3i>> northProfile = createGoldHeadNorth();

        northProfile.getFirst().addAll(getCopperCore());

        Set<MVec3i> goldHeadElement1left = Set.of(     // curved element
                new MVec3i(-2, 1, -1, Direction.SOUTH, powerCategory.GOLD.get()), // left
                new MVec3i(-2, 1, -2, Direction.WEST, powerCategory.GOLD.get()),  // left
                new MVec3i(-1, 1, -2, Direction.WEST, powerCategory.GOLD.get())  // left
        );
        Set<MVec3i> goldHeadElement1right = Set.of(
                new MVec3i(2, 1, -1, Direction.SOUTH, powerCategory.GOLD.get()),  // right
                new MVec3i(2, 1, -2, Direction.EAST, powerCategory.GOLD.get()),   // right
                new MVec3i(1, 1, -2, Direction.EAST, powerCategory.GOLD.get())    // right
        );

        northProfile.add(goldHeadElement1left);                                             // up left
        northProfile.add(goldHeadElement1right);                                            // up right
        northProfile.add(createNorthElement(goldHeadElement1left, new Vec3i(0, -2, 0)));    // down left
        northProfile.add(createNorthElement(goldHeadElement1right, new Vec3i(0, -2, 0)));   // down right

        Set<MVec3i> goldHeadElement2left = Set.of(     // straight element
                new MVec3i(-1, 2, -1, Direction.SOUTH, powerCategory.GOLD.get()),
                new MVec3i(-1, 2, -2, Direction.SOUTH ,powerCategory.GOLD.get())
        );

        northProfile.add(createNorthElement(goldHeadElement2left, new Vec3i(0, 0, 0)));     // up left
        northProfile.add(createNorthElement(goldHeadElement2left, new Vec3i(2, 0, 0)));     // up right
        northProfile.add(createNorthElement(goldHeadElement2left, new Vec3i(0, -4, 0)));    // down left
        northProfile.add(createNorthElement(goldHeadElement2left, new Vec3i(2, -4, 0)));    // down right


        Set<MVec3i> copperHeadElement = Set.of(    // up facing north
                new MVec3i(0, 1, -3, Direction.UP, powerCategory.COPPER.get()),
                new MVec3i(0, 2, -3, Direction.UP ,powerCategory.COPPER.get()),
                new MVec3i(0, 3, -3, Direction.UP, powerCategory.COPPER.get()),
                new MVec3i(0, 3, -2, Direction.SOUTH, powerCategory.COPPER.get()),
                new MVec3i(0, 3, -1, Direction.SOUTH, powerCategory.COPPER.get())
        );

        northProfile.add(createNorthElement(copperHeadElement, MVec3i.Angles.CLOCK_360));   // up
        northProfile.add(createNorthElement(copperHeadElement, MVec3i.Angles.CLOCK_180));   // down
        northProfile.add(createNorthElement(copperHeadElement, MVec3i.Angles.CLOCK_270));   // left
        northProfile.add(createNorthElement(copperHeadElement, MVec3i.Angles.CLOCK_90));    // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createCopperBodyNorth()
    {
        Vector<Set<MVec3i>> northProfile = createGoldBodyNorth();

        northProfile.getFirst().addAll(getCopperCore());    // core

        // curved element
        northProfile.add(Set.of(new MVec3i(-2, 1, 0, Direction.SOUTH, powerCategory.GOLD.get())));  // up left
        northProfile.add(Set.of(new MVec3i(2, 1, 0, Direction.SOUTH, powerCategory.GOLD.get())));   // up right
        northProfile.add(Set.of(new MVec3i(-2, -1, 0, Direction.SOUTH, powerCategory.GOLD.get()))); // down left
        northProfile.add(Set.of(new MVec3i(2, -1, 0, Direction.SOUTH, powerCategory.GOLD.get())));  // down right
        // straight element
        northProfile.add(Set.of(new MVec3i(-1, 2, 0, Direction.SOUTH, powerCategory.GOLD.get())));  // up left
        northProfile.add(Set.of(new MVec3i(1, 2, 0, Direction.SOUTH, powerCategory.GOLD.get())));   // up right
        northProfile.add(Set.of(new MVec3i(-1, -2, 0, Direction.SOUTH, powerCategory.GOLD.get()))); // down left
        northProfile.add(Set.of(new MVec3i(1, -2, 0, Direction.SOUTH, powerCategory.GOLD.get())));  // down right


        northProfile.add(Set.of(new MVec3i(0, 3, 0, Direction.SOUTH, powerCategory.COPPER.get())));     // up
        northProfile.add(Set.of(new MVec3i(0, -3, 0, Direction.SOUTH, powerCategory.COPPER.get())));    // down
        northProfile.add(Set.of(new MVec3i(-3, 0, 0, Direction.SOUTH, powerCategory.COPPER.get())));    // left
        northProfile.add(Set.of(new MVec3i(3, 0, 0, Direction.SOUTH, powerCategory.COPPER.get())));     // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createCopperTailNorth()
    {
        Vector<Set<MVec3i>> northProfile = createGoldTailNorth();

        northProfile.getFirst().addAll(getCopperCore());

        Set<MVec3i> goldTailElement1left = Set.of(
                new MVec3i(-2, 1, 1, Direction.SOUTH, powerCategory.GOLD.get()),  // left
                new MVec3i(-2, 1, 2, Direction.EAST, powerCategory.GOLD.get()),   // left
                new MVec3i(-1, 1, 2, Direction.EAST, powerCategory.GOLD.get())    // left
        );
        Set<MVec3i> goldTailElement1right = Set.of(
                new MVec3i(2, 1, 1, Direction.SOUTH, powerCategory.GOLD.get()),   // right
                new MVec3i(2, 1, 2, Direction.WEST, powerCategory.GOLD.get()),    // right
                new MVec3i(1, 1, 2, Direction.WEST, powerCategory.GOLD.get())     // right
        );

        northProfile.add(goldTailElement1left);                                             // up left
        northProfile.add(goldTailElement1right);                                            // up right
        northProfile.add(createNorthElement(goldTailElement1left, new Vec3i(0, -2, 0)));    // down left
        northProfile.add(createNorthElement(goldTailElement1right, new Vec3i(0, -2, 0)));   // down right

        Set<MVec3i> goldTailElement2left = Set.of(     // straight element
                new MVec3i(-1, 2, 1, Direction.SOUTH, powerCategory.GOLD.get()),
                new MVec3i(-1, 2, 2, Direction.SOUTH, powerCategory.GOLD.get())
        );

        northProfile.add(createNorthElement(goldTailElement2left, new Vec3i(0, 0, 0)));     // up left
        northProfile.add(createNorthElement(goldTailElement2left, new Vec3i(2, 0, 0)));     // up right
        northProfile.add(createNorthElement(goldTailElement2left, new Vec3i(0, -4, 0)));    // down left
        northProfile.add(createNorthElement(goldTailElement2left, new Vec3i(2, -4, 0)));    // down right


        Set<MVec3i> copperTailElement = Set.of(      // up facing north
                new MVec3i(0, 1, 3, Direction.DOWN, powerCategory.COPPER.get()),
                new MVec3i(0, 2, 3, Direction.DOWN, powerCategory.COPPER.get()),
                new MVec3i(0, 3, 3, Direction.DOWN, powerCategory.COPPER.get()),
                new MVec3i(0, 3, 2, Direction.SOUTH, powerCategory.COPPER.get()),
                new MVec3i(0, 3, 1, Direction.SOUTH, powerCategory.COPPER.get())
        );

        northProfile.add(createNorthElement(copperTailElement, MVec3i.Angles.CLOCK_360));   // up
        northProfile.add(createNorthElement(copperTailElement, MVec3i.Angles.CLOCK_180));   // down
        northProfile.add(createNorthElement(copperTailElement, MVec3i.Angles.CLOCK_270));   // left
        northProfile.add(createNorthElement(copperTailElement, MVec3i.Angles.CLOCK_90));    // right

        return northProfile;
    }

    private static Vector<Set<MVec3i>> createMagnetHeadNorth()
    {
        Vector<Set<MVec3i>> wipedNorthProfile = new Vector<>();

        for (var forceElement : createCopperHeadNorth())
        {
            wipedNorthProfile.add(MVec3i.wipePowerDeltas(forceElement, powerCategory.MAGNET.get()));
        }

        wipedNorthProfile.getFirst().clear();   // clears core (no core for magnet block

        Set<MVec3i> genericHeadElement = createNorthElement(new MVec3i(0, 0, -1, Direction.NORTH, powerCategory.MAGNET.get()),
                                                            new MVec3i(0, 0, -15, Direction.NORTH, powerCategory.MAGNET.get()));

        wipedNorthProfile.add(genericHeadElement);  // head
        wipedNorthProfile.add(Set.of());    // tail

        return wipedNorthProfile;
    }

    private static Vector<Set<MVec3i>> createMagnetBodyNorth()
    {
        Vector<Set<MVec3i>> wipedNorthProfile = new Vector<>();

        // no additional cores

        for (var forceElement : createCopperBodyNorth())
        {
            wipedNorthProfile.add(MVec3i.wipePowerDeltas(forceElement, powerCategory.MAGNET.get()));
        }

        wipedNorthProfile.add(Set.of());    // head
        wipedNorthProfile.add(Set.of());    // tail

        wipedNorthProfile.getFirst().clear();

        return wipedNorthProfile;
    }

    private static Vector<Set<MVec3i>> createMagnetTailNorth()
    {
        Vector<Set<MVec3i>> wipedNorthProfile = new Vector<>();

        // no additional cores

        for (var forceElement : createCopperTailNorth())
        {
            wipedNorthProfile.add(MVec3i.wipePowerDeltas(forceElement, powerCategory.MAGNET.get()));
        }

        Set<MVec3i> genericTailElement = createNorthElement(new MVec3i(0, 0, 1, Direction.NORTH, powerCategory.MAGNET.get()),
                                                            new MVec3i(0, 0, 15, Direction.NORTH, powerCategory.MAGNET.get()));

        wipedNorthProfile.add(Set.of());    // head
        wipedNorthProfile.add(genericTailElement);  // tail

        wipedNorthProfile.getFirst().clear();

        return wipedNorthProfile;
    }

    public enum powerCategory
    {
        IRON(0),
        GOLD(-1),
        COPPER(-2),
        MAGNET(0),
        GENERIC(null);

        private final Integer powerDelta;

        powerCategory(Integer delta) { this.powerDelta = delta; }

        public Integer get() {  return this.powerDelta; }
    }

    private static Set<MVec3i> createNorthElement(MVec3i pos1, MVec3i pos2)
    {
        Set<MVec3i> northElement = new HashSet<>();

        Direction pos1Direction = pos1.getForceDirection();
        int pos1PowerDelta = pos1.getPowerDelta();

        int bigX = Math.max(pos1.getX(), pos2.getX());
        int smallX = Math.min(pos1.getX(), pos2.getX());
        int bigY = Math.max(pos1.getY(), pos2.getY());
        int smallY = Math.min(pos1.getY(), pos2.getY());
        int bigZ = Math.max(pos1.getZ(), pos2.getZ());
        int smallZ = Math.min(pos1.getZ(), pos2.getZ());

        for (int x = smallX;x <= bigX;x++)
        {
            for (int y = smallY;y <= bigY;y++)
            {
                for (int z = smallZ;z <= bigZ;z++)
                {
                    northElement.add(new MVec3i(x, y, z, pos1Direction, pos1PowerDelta));
                }
            }
        }

        return northElement;
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
