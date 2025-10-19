package net.devs.electromod;

import net.devs.electromod.block.ModBlocks;
import net.devs.electromod.block.custom.magnetic.MagneticForce.AbstractDetectorBlockEntity;
import net.devs.electromod.block.custom.magnetic.MagneticForce.AbstractMagneticBlockEntity;
import net.devs.electromod.block.custom.magnetic.MagneticForce.ForceProfile;
import net.devs.electromod.block.custom.magnetic.MagneticForce.MagneticForceInteractor;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.devs.electromod.components.ModDataComponentTypes;
import net.devs.electromod.item.ModItemGroups;
import net.devs.electromod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElectroMod implements ModInitializer
{
    public static final String MOD_ID = "electromod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
    {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerModItemGroups();
        ModDataComponentTypes.registerModDataComponentTypes();
        ModBlockEntities.registerModBlockEntities();

        ForceProfile.registerForceProfiles();

        // register events
        // block entity load events
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((
        (blockEntity, world) ->
        {
            if (world.isClient()) return;

            if (blockEntity instanceof AbstractMagneticBlockEntity magneticBE)
            { magneticBE.blockentityLoaded(); }

            else if (blockEntity instanceof AbstractDetectorBlockEntity detectorBE)
            { detectorBE.blockentityLoaded(); }

        }));

        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((
        (blockEntity, world) ->
        {
            if (world.isClient()) return;

            if (blockEntity instanceof AbstractMagneticBlockEntity forceBE)
            { forceBE.blockentityUnloaded(); }

            else if (blockEntity instanceof AbstractDetectorBlockEntity detectorBE)
            { detectorBE.blockentityUnloaded(); }
        }));

        // server load events
        ServerWorldEvents.LOAD.register((MagneticForceInteractor::initPosMap));
        ServerWorldEvents.UNLOAD.register((MagneticForceInteractor::initPosMap));

        //Test();
	}

    private static void Test()
    {

    }
}
