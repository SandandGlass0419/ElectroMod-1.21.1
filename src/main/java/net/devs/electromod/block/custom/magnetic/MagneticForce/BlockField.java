package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.ElectroMod;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockField
{                  // magneticPos, field
    private final Map<BlockPos, MagneticField> Fields = new HashMap<>();

    public void putField(BlockPos magneticPos, int magneticPower, Direction forceDirection)
    { this.Fields.put(magneticPos, new MagneticField(magneticPower, forceDirection)); }

    public void putField(BlockPos magneticPos, MagneticField field)
    { this.Fields.put(magneticPos, field); }

    public Map<BlockPos, MagneticField> getFields()
    { return this.Fields; }

    public MagneticField get(BlockPos magneticPos)
    { return this.Fields.get(magneticPos); }

    public void removeField(BlockPos magneticPos)
    { this.Fields.remove(magneticPos); }

    public Pair<Integer, Integer> getPureAdditive(Set<BlockPos> excludedPos)
    {
        int netPower = 0;
        int netCount = 0;

        ElectroMod.LOGGER.info("size: {}", this.Fields.size());
        for (var magneticPos : this.Fields.keySet())
        {
            if (excludedPos.contains(magneticPos)) { ElectroMod.LOGGER.info("passed: {}", magneticPos); continue; }

            switch (this.Fields.get(magneticPos).getForceDirection().getDirection())
            {
                case POSITIVE:
                    netPower += this.Fields.get(magneticPos).getMagneticPower();
                    netCount++;
                    break;

                case NEGATIVE:
                    netPower -= this.Fields.get(magneticPos).getMagneticPower();
                    netCount--;
                    break;
            }
        }

        return new Pair<>(netPower, netCount);
    }

    public static int normalize(Pair<Integer, Integer> pureAdditive, int normalizer)
    {
        return Math.abs(pureAdditive.getLeft() - pureAdditive.getRight() * normalizer);
    }

    @Deprecated
    public int getMaxAdditive(Set<BlockPos> excludedPos)
    {
        int maxPositive = 0;
        int maxNegative = 0;

        for (var magneticPos : this.Fields.keySet())
        {
            if (excludedPos.contains(magneticPos)) continue;

            switch (this.Fields.get(magneticPos).getForceDirection().getDirection())
            {
                case POSITIVE -> maxPositive = Math.max(maxPositive, this.Fields.get(magneticPos).getMagneticPower());
                case NEGATIVE -> maxNegative = Math.max(maxNegative, this.Fields.get(magneticPos).getMagneticPower());
            }
        }

        return maxPositive - maxNegative;
    }
}
