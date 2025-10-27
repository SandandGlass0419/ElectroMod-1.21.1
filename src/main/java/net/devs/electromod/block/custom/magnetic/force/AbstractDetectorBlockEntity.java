package net.devs.electromod.block.custom.magnetic.force;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractDetectorBlockEntity extends BlockEntity
{
    private boolean startWatch = false;
    private final Map<BlockPos, Set<BlockPos>> Watch = new HashMap<>();

    public boolean getStartWatch() { return this.startWatch; }
    public Map<BlockPos, Set<BlockPos>> getWatch() { return this.Watch; }

    public AbstractDetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void blockentityLoaded()
    {
        BlockField blockField = MagneticForceInteractor.detectorPlacementCheck(this.world, this.pos);
        MagneticForceInteractor.subscribeDetectorBlock(this.world, this.pos, blockField);

        setupWatch();

        UpdateWatchCallBack.UPDATED.register(this::updateWatchElement);
        UpdateWatchCallBack.REMOVED.register(this::removeWatchElement);
    }

    public void blockentityUnloaded()
    {
        MagneticForceInteractor.unsubscribeDetectorBlock(this.world, this.pos);
    }

    public void setupWatch()
    {
        BlockField field = MagneticForceInteractor.getBlockField(this.world, this.pos);
        if (field == null) return;

        for (var magneticPos : field.getFields().keySet())
        {
            updateHelper(this.pos, magneticPos, field.get(magneticPos));
        }

        this.startWatch = true;
    }

    public void updateWatchElement(BlockPos detectorPos, BlockPos magneticPos)
    {
        if (!this.pos.equals(detectorPos)) return;

        BlockField field = MagneticForceInteractor.getBlockField(this.world, this.pos);
        if (field == null) return;

        updateHelper(detectorPos, magneticPos, field.get(magneticPos));
    }

    public void removeWatchElement(BlockPos detectorPos, BlockPos magneticPos)
    {
        if (!this.pos.equals(detectorPos)) return;

        this.Watch.remove(magneticPos);

        //ElectroMod.LOGGER.info("removed watch: {}, {}", detectorPos, magneticPos);
    }

    private void updateHelper(BlockPos detectorPos, BlockPos magneticPos, MagneticField detectorField)
    {
        if(!additionalConditions(detectorField)) return;

        ForceProfile profile = MagneticForceInteractor.getForceProfile(MagneticForceInteractor.getField(this.world, magneticPos));
        Integer index = profile.getIndex(magneticPos, this.pos);
        if (index == null) return;

        this.Watch.put(magneticPos, profile.getWatch(index, magneticPos));
        //ElectroMod.LOGGER.info("updated watch: {}, {}", detectorPos, magneticPos);
        //ElectroMod.LOGGER.info("added: {}", profile.getWatch(index , magneticPos));
    }

    public boolean additionalConditions(MagneticField field)
    {
        return field != null;
    }
}
