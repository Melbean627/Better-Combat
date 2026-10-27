package dev.melbean627.offhandswordlunge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public final class OffhandSwordLunge implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(LungePayload.ID, LungePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(LungePayload.ID, (payload, context) -> {
            var player = context.player();
            ItemStack sword = player.getOffHandStack();
            if (player.isSpectator() || !(sword.getItem() instanceof SwordItem)) {
                return;
            }

            applyLunge(player);
            sword.damage(10, player, EquipmentSlot.OFFHAND);
        });
    }

    static void applyLunge(PlayerEntity player) {
        double yaw = Math.toRadians(player.getYaw());
        player.addVelocity(-Math.sin(yaw) * 1.2, 0.15, Math.cos(yaw) * 1.2);
    }
}
