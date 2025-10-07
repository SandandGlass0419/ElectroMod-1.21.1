package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.StringIdentifiable;

public enum ForceProfile implements StringIdentifiable
{
    DISTANCE_ALL("all"),
    DISTANCE_SIDE("side"),
    DISTANCE_FRONT("front"),
    DISTANCE_BACK("back");

    private String name;

    ForceProfile(String name) {
        this.name = name;
    }

    @Override public String asString() {
        return name;
    }
}
