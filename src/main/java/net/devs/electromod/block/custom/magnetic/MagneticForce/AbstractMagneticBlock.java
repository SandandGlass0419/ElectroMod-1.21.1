package net.devs.electromod.block.custom.magnetic.MagneticForce;

import net.devs.electromod.block.custom.magnetic.CoilBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMagneticBlock extends BlockWithEntity implements BlockEntityProvider
{
    public AbstractMagneticBlock(Settings settings) {
        super(settings);
    }

    protected BlockState[] getNeighborState(World world, BlockPos pos, Direction direction)
    {
        return new BlockState[] { world.getBlockState(pos.offset(direction)), world.getBlockState(pos.offset(direction.getOpposite())) };
    }

    public ForceProfile getForceProfile(World world, BlockPos pos, Direction direction, int magneticPower)
    {
        Boolean[] res = needsProfileElements(getNeighborState(world, pos, direction), direction);

        int headPowerCategory = ForceProfile.getPowerCategory(res[0] ? magneticPower : 0);
        int bodyPowerCategory = ForceProfile.getPowerCategory(magneticPower);
        int tailPowerCategory = ForceProfile.getPowerCategory(res[1] ? magneticPower : 0);

        return ForceProfile.createForceProfile(headPowerCategory, bodyPowerCategory, tailPowerCategory, direction);
    }

    protected Boolean[] needsProfileElements(BlockState[] neighborStates, Direction direction)
    {
        List<Boolean> result = new ArrayList<>();

        for (BlockState state : neighborStates)
        {
            if (state.getBlock() instanceof CoilBlock && state.get(Properties.FACING) == direction)
            { result.add(true); }

            else { result.add(false); }
        }

        return result.toArray(Boolean[]::new);
    }
}
