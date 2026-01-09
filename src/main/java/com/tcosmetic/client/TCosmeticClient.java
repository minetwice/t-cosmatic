package com.tcosmetic.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.tcosmetic.network.CapeSyncPayload;
import java.io.File;

public class TCosmeticClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Initialize Cape Manager
        CapeManager.init();

        // Handle received cape updates from other players (via server)
        ClientPlayNetworking.registerGlobalReceiver(CapeSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                CapeManager.setPlayerCape(payload.playerUuid(), payload.capeName());
            });
        });
    }
}
