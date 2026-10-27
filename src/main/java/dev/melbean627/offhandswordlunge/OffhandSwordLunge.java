package dev.melbean627.offhandswordlunge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

public final class OffhandSwordLunge implements ModInitializer {
    @Override
    public void onInitialize() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack sword = player.getStackInHand(hand);
            if (player.isSpectator()
                    || hand != Hand.OFF_HAND
                    || !(sword.getItem() instanceof SwordItem)) {
                return TypedActionResult.pass(sword);
            }

            if (!world.isClient) {
                double yaw = Math.toRadians(player.getYaw());
                player.addVelocity(-Math.sin(yaw) * 1.2, 0.15, Math.cos(yaw) * 1.2);
                sword.damage(10, player, EquipmentSlot.OFFHAND);
            }

            return TypedActionResult.success(sword);
        });
    }
}
