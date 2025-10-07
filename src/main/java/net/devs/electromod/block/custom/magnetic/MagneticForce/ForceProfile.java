package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.StringIdentifiable;

public enum ForceProfile implements StringIdentifiable
{
    ALL("all"),
    SIDE("side"),
    FRONT("front"),
    BACK("back");

    private String name;

    ForceProfile(String name) {
        this.name = name;
    }

    @Override public String asString() {
        return name;
    }
}
