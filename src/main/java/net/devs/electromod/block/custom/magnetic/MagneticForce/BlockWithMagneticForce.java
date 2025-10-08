package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.components.ModDataComponentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;

public interface BlockWithMagneticForce
{
    int min_force = ModDataComponentTypes.min_force;
    int max_force = ModDataComponentTypes.max_force;

    BooleanProperty MAGNETIC = BooleanProperty.of("magnetic");

    ForceComponents getForceComponents(BlockState state);
}
