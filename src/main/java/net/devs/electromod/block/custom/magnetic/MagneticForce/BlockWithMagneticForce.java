package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.components.ModDataComponentTypes;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public interface BlockWithMagneticForce
{
    int min_force = ModDataComponentTypes.min_force;
    int max_force = ModDataComponentTypes.max_force;

    IntProperty MAGNET_FORCE = IntProperty.of("magnet_force", min_force, max_force);
    DirectionProperty FORCE_DIRECTION = Properties.FACING;
    EnumProperty<ForceProfile> DISTANCE_FORCE_PROFILE = EnumProperty.of("distance_force_profile", ForceProfile.class);

    int calculateForce(BlockState state);
}
