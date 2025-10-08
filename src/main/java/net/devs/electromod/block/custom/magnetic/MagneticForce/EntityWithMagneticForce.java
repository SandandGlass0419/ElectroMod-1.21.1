package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.components.ModDataComponentTypes;
import net.minecraft.block.BlockState;

public interface EntityWithMagneticForce
{
    int min_force = ModDataComponentTypes.min_force;
    int max_force = ModDataComponentTypes.max_force;



    int calculateForce(BlockState state);
}
