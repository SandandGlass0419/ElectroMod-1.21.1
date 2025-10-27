package net.devs.electromod.block.custom.magnetic.force;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;

public interface UpdateWatchCallBack
{
    Event<UpdateWatchCallBack> UPDATED = EventFactory.createArrayBacked(
            UpdateWatchCallBack.class,
            (listeners) -> (BlockPos detectorPos, BlockPos magneticPos) -> {
                for (UpdateWatchCallBack listener : listeners) {
                    listener.Broadcast(detectorPos, magneticPos);
                }
            }
    );

    Event<UpdateWatchCallBack> REMOVED = EventFactory.createArrayBacked(
            UpdateWatchCallBack.class,
            (listeners) -> (BlockPos detectorPos, BlockPos magneticPos) -> {
                for (UpdateWatchCallBack listener : listeners) {
                    listener.Broadcast(detectorPos, magneticPos);
                }
            }
    );

    void Broadcast(BlockPos detectorPos, BlockPos magneticPos);
}
