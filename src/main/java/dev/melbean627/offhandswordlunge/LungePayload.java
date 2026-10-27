package dev.melbean627.offhandswordlunge;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LungePayload() implements CustomPayload {
    public static final Id<LungePayload> ID = new Id<>(Identifier.of("offhand_sword_lunge", "lunge"));
    public static final PacketCodec<RegistryByteBuf, LungePayload> CODEC = PacketCodec.unit(new LungePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
