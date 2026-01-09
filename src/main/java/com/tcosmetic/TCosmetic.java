package com.tcosmetic;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;
import com.tcosmetic.network.CapeSyncPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCosmetic implements ModInitializer {
	public static final String MOD_ID = "tcosmetic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("TCosmetic initialized!");
        // Register networking payload
        PayloadTypeRegistry.playS2C().register(CapeSyncPayload.ID, CapeSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CapeSyncPayload.ID, CapeSyncPayload.CODEC);
	}
}
