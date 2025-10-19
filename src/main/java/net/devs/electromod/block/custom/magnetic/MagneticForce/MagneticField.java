package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.minecraft.util.math.Direction;

public class MagneticField
{
    private int magneticPower;
    private Direction forceDirection;

    public void setMagneticPower(int magneticPower) { this.magneticPower = magneticPower; }
    public int getMagneticPower() { return this.magneticPower; }

    public void setForceDirection(Direction forceDirection) { this.forceDirection = forceDirection; }
    public void setForceDirection(int forceID) { this.forceDirection = Direction.byId(forceID); }

    public Direction getForceDirection() { return this.forceDirection; }

    public MagneticField(int magneticPower, Direction forceDirection)
    {
        this.magneticPower = magneticPower;
        this.forceDirection = forceDirection;
    }
}
