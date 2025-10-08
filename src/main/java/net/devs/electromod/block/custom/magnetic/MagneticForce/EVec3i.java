package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class EVec3i extends Vec3i
{
    public EVec3i(int x, int y, int z) { super(x, y, z); }
    public EVec3i(Vec3i vec3i) { super(vec3i.getX(), vec3i.getY(), vec3i.getZ());}

    public Set<EVec3i> add(Set<EVec3i> deltas)
    {
        Set<EVec3i> poses = new HashSet<>();
        
        for (EVec3i delta : deltas)
        {
            poses.add(new EVec3i(this.add(delta)));
        }
        
        return poses;
    }

    public EVec3i rotate(Direction.Axis axis, int angleOrdinal)
    {
        Vector3f vectorAxis = toVector3f(axis);
        Vector3f vectorPos = toVector3f(this);
        double angle = 90 * angleOrdinal;

        Quaternionf quat = new Quaternionf().fromAxisAngleRad(vectorAxis.normalize(), (float) Math.toRadians(angle));
        return toEVec3i(quat.transform(vectorPos));
    }

    public static Set<EVec3i> rotateDeltas(Set<EVec3i> deltas, Direction.Axis axis, int angleOrdinal)
    {
        Set<EVec3i> vecSet = new HashSet<>();

        Vector3f vectorAxis = toVector3f(axis);
        double angle = 90 * angleOrdinal;
        Quaternionf quat = new Quaternionf().fromAxisAngleRad(vectorAxis.normalize(), (float) Math.toRadians(angle));

        for (EVec3i vec3i : deltas)
        {
            vecSet.add(toEVec3i(quat.transform(toVector3f(vec3i))));
        }

        return vecSet;
    }

    public enum Angles
    {
        CLOCK_360,
        CLOCK_270,
        CLOCK_180,
        CLOCK_90;
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

    private static EVec3i toEVec3i(Vector3f vector3f)
    {
        return new EVec3i((int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z());
    }

    public BlockPos toBlockPos() { return new BlockPos(this); }
}
