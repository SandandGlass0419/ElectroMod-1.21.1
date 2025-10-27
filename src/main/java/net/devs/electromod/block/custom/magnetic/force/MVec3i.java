package net.devs.electromod.block.custom.magnetic.force;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class MVec3i extends Vec3i
{
    public MVec3i(int x, int y, int z, Direction forceDirection, int powerDelta)
    {
        super(x, y, z);
        this.forceDirection = forceDirection;
        this.powerDelta = powerDelta;
    }

    public MVec3i(Vec3i vec3i, Direction forceDirection, int powerDelta)
    {
        super(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        this.forceDirection = forceDirection;
        this.powerDelta = powerDelta;
    }

    private final Direction forceDirection;
    private final int powerDelta;

    public Direction getForceDirection() { return forceDirection; }
    public int getPowerDelta() { return powerDelta; }

    public static Set<MVec3i> add(Set<MVec3i> deltas, Vec3i vec3i)
    {
        Set<MVec3i> poses = new HashSet<>();

        for (MVec3i delta : deltas)
        {
            poses.add(new MVec3i(delta.add(vec3i), delta.getForceDirection(), delta.getPowerDelta()));
        }

        return poses;
    }

    public static Set<BlockPos> add(Set<MVec3i> deltas, BlockPos pos)
    {
        Set<BlockPos> poses = new HashSet<>();

        for (MVec3i delta : deltas)
        {
            poses.add(pos.add(delta));
        }

        return poses;
    }

    public MVec3i rotate90(Direction.Axis axis, int angleOrdinal)
    {
        Vector3f vectorAxis = toVector3f(axis);
        Vector3f vectorPos = toVector3f(this);
        Vector3f vectorDirection = toVector3f(this.forceDirection);
        double angle = 90 * angleOrdinal;

        Quaternionf quat = new Quaternionf().fromAxisAngleRad(vectorAxis.normalize(), (float) Math.toRadians(angle));
        return toMVec3i(quat.transform(vectorPos), toDirection(quat.transform(vectorDirection)), this.powerDelta);
    }

    public static Set<MVec3i> rotate90(Set<MVec3i> deltas, Direction.Axis axis, int angleOrdinal)
    {
        Set<MVec3i> vecSet = new HashSet<>();

        Vector3f vectorAxis = toVector3f(axis);
        Vector3f vectorPos;
        Vector3f vectorDirection;
        double angle = 90 * angleOrdinal;

        Quaternionf quat = new Quaternionf().fromAxisAngleRad(vectorAxis.normalize(), (float) Math.toRadians(angle));

        for (MVec3i mvec3i : deltas)
        {
            vectorPos = toVector3f(mvec3i);
            vectorDirection = toVector3f(mvec3i.getForceDirection());

            vecSet.add(toMVec3i(
                quat.transform(vectorPos),
                toDirection(quat.transform(vectorDirection)),
                mvec3i.getPowerDelta()));
        }

        return vecSet;
    }

    public static Set<MVec3i> wipePowerDeltas(Set<MVec3i> deltas, int newPowerDelta)
    {
        Set<MVec3i> newDeltas = new HashSet<>();

        for (MVec3i mVec3i : deltas)
        {
            newDeltas.add(new MVec3i(mVec3i, mVec3i.getForceDirection(), newPowerDelta));
        }

        return newDeltas;
    }

    public enum Angles
    {
        CLOCK_360,
        CLOCK_270,
        CLOCK_180,
        CLOCK_90
    }

    public static Vector3f toVector3f(Direction direction)
    {
        return toVector3f(direction.getVector());
    }

    public static Vector3f toVector3f(Direction.Axis axis)
    {
        return switch (axis)
        {
            case X -> new Vector3f(1, 0, 0);
            case Y -> new Vector3f(0, 1, 0);
            case Z -> new Vector3f(0, 0, 1);
        };
    }

    public static Vector3f toVector3f(Vec3i vec3i)
    {
        return new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    private static MVec3i toMVec3i(Vector3f vector3f, Direction forceDirection, int powerDelta)
    {
        return new MVec3i((int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z(), forceDirection, powerDelta);
    }

    private static Direction toDirection(Vector3f vector3f)
    {
        return Direction.fromVector((int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z());
    }

    public BlockPos toBlockPos() { return new BlockPos(this); }

    public static Set<BlockPos> toBlockPos(Set<MVec3i> mvec3i)
    {
        Set<BlockPos> positions = new HashSet<>();

        for (var vec : mvec3i)
        {
            positions.add(vec.toBlockPos());
        }

        return positions;
    }

    public MagneticField toMagneticField(int magneticPower)
    {
        return new MagneticField(magneticPower + this.powerDelta, this.forceDirection);
    }
}
