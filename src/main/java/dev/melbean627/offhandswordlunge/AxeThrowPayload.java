package dev.melbean627.offhandswordlunge;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AxeThrowPayload() implements CustomPayload {
    public static final Id<AxeThrowPayload> ID = new Id<>(Identifier.of("offhand_sword_lunge", "throw_axe"));
    public static final PacketCodec<RegistryByteBuf, AxeThrowPayload> CODEC =
            PacketCodec.unit(new AxeThrowPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
