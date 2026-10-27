package dev.melbean627.offhandswordlunge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OffhandSwordLunge implements ModInitializer {
    private static final int LUNGE_COOLDOWN_TICKS = 40;
    private static final Map<UUID, Integer> LUNGE_COOLDOWNS = new HashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(LungePayload.ID, LungePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AxeThrowPayload.ID, AxeThrowPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(LungePayload.ID, (payload, context) -> {
            var player = context.player();
            ItemStack sword = player.getOffHandStack();
            if (player.isSpectator() || !(sword.getItem() instanceof SwordItem)) {
                return;
            }

            int cooldownUntil = LUNGE_COOLDOWNS.getOrDefault(player.getUuid(), 0);
            if (player.age < cooldownUntil) {
                return;
            }

            LUNGE_COOLDOWNS.put(player.getUuid(), player.age + LUNGE_COOLDOWN_TICKS);
            applyLunge(player);
            sword.damage(10, player, EquipmentSlot.OFFHAND);
        });
        ServerPlayNetworking.registerGlobalReceiver(AxeThrowPayload.ID, (payload, context) ->
                throwAxe(context.player()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                LUNGE_COOLDOWNS.remove(handler.player.getUuid()));
    }

    static void applyLunge(PlayerEntity player) {
        double yaw = Math.toRadians(player.getYaw());
        player.addVelocity(-Math.sin(yaw) * 1.2, 0.15, Math.cos(yaw) * 1.2);
    }

    private static void throwAxe(PlayerEntity player) {
        if (player.isSpectator()) {
            return;
        }

        Hand hand = player.getMainHandStack().getItem() instanceof AxeItem
                ? Hand.MAIN_HAND
                : player.getOffHandStack().getItem() instanceof AxeItem ? Hand.OFF_HAND : null;
        if (hand == null) {
            return;
        }

        ItemStack heldAxe = player.getStackInHand(hand);
        ItemEntity thrownAxe = new ItemEntity(
                player.getWorld(),
                player.getX(),
                player.getEyeY() - 0.15,
                player.getZ(),
                heldAxe.copyWithCount(1));
        Vec3d lookDirection = player.getRotationVec(1.0F);
        thrownAxe.setVelocity(lookDirection.x * 1.5, lookDirection.y * 1.5, lookDirection.z * 1.5);
        thrownAxe.setPickupDelay(10);

        heldAxe.decrement(1);
        if (heldAxe.isEmpty()) {
            player.setStackInHand(hand, ItemStack.EMPTY);
        }
        player.getWorld().spawnEntity(thrownAxe);
    }
}
