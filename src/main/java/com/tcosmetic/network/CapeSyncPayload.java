package com.tcosmetic.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record CapeSyncPayload(UUID playerUuid, String capeName) implements CustomPayload {
    public static final CustomPayload.Id<CapeSyncPayload> ID = new CustomPayload.Id<>(Identifier.of("tcosmetic", "cape_sync"));

    public static final PacketCodec<RegistryByteBuf, CapeSyncPayload> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, CapeSyncPayload::playerUuid,
            PacketCodecs.STRING, CapeSyncPayload::capeName,
            CapeSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
