package com.tcosmetic.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import com.tcosmetic.TCosmetic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class CapeManager {
    private static final Map<String, Identifier> LOADED_CAPES = new HashMap<>();
    private static final Map<UUID, String> PLAYER_CAPES = new HashMap<>();
    public static final File TCOSMETIC_DIR = new File(MinecraftClient.getInstance().runDirectory, "TCosmetic");
    public static final File CAPES_DIR = new File(TCOSMETIC_DIR, "capes");
    
    public static String selectedCape = null;

    public static void init() {
        if (!TCOSMETIC_DIR.exists()) TCOSMETIC_DIR.mkdirs();
        if (!CAPES_DIR.exists()) CAPES_DIR.mkdirs();
        reloadCapes();
    }

    public static void reloadCapes() {
        LOADED_CAPES.clear();
        File[] files = CAPES_DIR.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null) return;

        for (File file : files) {
            try {
                FileInputStream stream = new FileInputStream(file);
                NativeImage image = NativeImage.read(stream);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                String name = file.getName().replace(".png", "");
                Identifier id = Identifier.of(TCosmetic.MOD_ID, "capes/" + name.toLowerCase());
                
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
                LOADED_CAPES.put(name, id);
                stream.close();
            } catch (IOException e) {
                TCosmetic.LOGGER.error("Failed to load cape: " + file.getName(), e);
            }
        }
    }

    public static Map<String, Identifier> getLoadedCapes() {
        return LOADED_CAPES;
    }

    public static void setPlayerCape(UUID uuid, String capeName) {
        if (capeName == null || capeName.isEmpty()) {
            PLAYER_CAPES.remove(uuid);
        } else {
            PLAYER_CAPES.put(uuid, capeName);
        }
    }

    public static Identifier getPlayerCape(UUID uuid) {
        // If it's the local player, use the selected cape
        if (MinecraftClient.getInstance().player != null && uuid.equals(MinecraftClient.getInstance().player.getUuid())) {
             if (selectedCape != null && LOADED_CAPES.containsKey(selectedCape)) {
                 return LOADED_CAPES.get(selectedCape);
             }
        }
        
        // For other players
        String capeName = PLAYER_CAPES.get(uuid);
        if (capeName != null && LOADED_CAPES.containsKey(capeName)) {
            return LOADED_CAPES.get(capeName);
        }
        return null;
    }
}
