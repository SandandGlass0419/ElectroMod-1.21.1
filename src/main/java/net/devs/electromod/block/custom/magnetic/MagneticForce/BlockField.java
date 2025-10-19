package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public class BlockField
{                     // origin block, field
    private final Map<BlockPos, MagneticField> Fields = new HashMap<>();

    public void putField(BlockPos magneticPos, int magneticPower, Direction forceDirection)
    { this.Fields.put(magneticPos, new MagneticField(magneticPower, forceDirection)); }

    public void putField(BlockPos magneticPos, MagneticField field)
    { this.Fields.put(magneticPos, field); }

    public Map<BlockPos, MagneticField> getFields()
    { return this.Fields; }

    public void removeField(BlockPos magneticPos)
    { this.Fields.remove(magneticPos); }
}
