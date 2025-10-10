package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class MagneticForceInteractor
{
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
