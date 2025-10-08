package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class MVec3i extends Vec3i
{
    public MVec3i(int x, int y, int z, Direction forceDirection)
    {
        super(x, y, z);
        this.forceDirection = forceDirection;
    }

    public MVec3i(Vec3i vec3i, Direction forceDirection)
    {
        super(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        this.forceDirection = forceDirection;
    }

    private final Direction forceDirection;

    public Direction getForceDirection() { return forceDirection; }

    public Set<MVec3i> add(Set<MVec3i> deltas)
    {
        Set<MVec3i> poses = new HashSet<>();
        
        for (MVec3i delta : deltas)
        {
            poses.add(new MVec3i(this.add(delta), this.forceDirection));
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
        return toMVec3i(quat.transform(vectorPos), toDirection(quat.transform(vectorDirection)));
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

            vecSet.add(
                    toMVec3i(quat.transform(vectorPos),
                             toDirection(quat.transform(vectorDirection))));
        }

        return vecSet;
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

    private static MVec3i toMVec3i(Vector3f vector3f, Direction forceDirection)
    {
        return new MVec3i((int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z(), forceDirection);
    }

    private static Direction toDirection(Vector3f vector3f)
    {
        return Direction.fromVector((int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z());
    }

    public BlockPos toBlockPos() { return new BlockPos(this); }
}
