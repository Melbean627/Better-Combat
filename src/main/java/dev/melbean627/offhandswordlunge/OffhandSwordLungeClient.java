package dev.melbean627.offhandswordlunge;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;

public final class OffhandSwordLungeClient implements ClientModInitializer {
    private static final int LUNGE_COOLDOWN_TICKS = 40;
    private static final int AXE_THROW_CHARGE_TICKS = 11;

    private boolean useKeyWasPressed;
    private boolean attackKeyWasPressed;
    private boolean axeThrowSent;
    private int lungeCooldownTicks;
    private int axeChargeTicks;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (lungeCooldownTicks > 0) {
                lungeCooldownTicks--;
            }

            if (client.player == null) {
                useKeyWasPressed = false;
                attackKeyWasPressed = false;
                axeThrowSent = false;
                axeChargeTicks = 0;
                lungeCooldownTicks = 0;
                return;
            }

            boolean useKeyPressed = client.options.useKey.isPressed();
            if (useKeyPressed
                    && !useKeyWasPressed
                    && client.currentScreen == null
                    && !client.player.isSpectator()
                    && lungeCooldownTicks == 0
                    && client.player.getOffHandStack().getItem() instanceof SwordItem) {
                OffhandSwordLunge.applyLunge(client.player);
                ClientPlayNetworking.send(new LungePayload());
                lungeCooldownTicks = LUNGE_COOLDOWN_TICKS;
            }
            useKeyWasPressed = useKeyPressed;

            boolean attackKeyPressed = client.options.attackKey.isPressed();
            if (client.currentScreen != null || client.player.isSpectator()) {
                axeChargeTicks = 0;
                axeThrowSent = false;
            } else if (attackKeyPressed && !attackKeyWasPressed) {
                axeChargeTicks = isHoldingAxe(client.player) ? 1 : 0;
                axeThrowSent = false;
            } else if (attackKeyPressed && axeChargeTicks > 0 && !axeThrowSent) {
                if (!isHoldingAxe(client.player)) {
                    axeChargeTicks = 0;
                } else if (++axeChargeTicks >= AXE_THROW_CHARGE_TICKS) {
                    ClientPlayNetworking.send(new AxeThrowPayload());
                    axeThrowSent = true;
                }
            } else if (!attackKeyPressed) {
                axeChargeTicks = 0;
                axeThrowSent = false;
            }
            attackKeyWasPressed = attackKeyPressed;
        });
    }

    private static boolean isHoldingAxe(net.minecraft.client.network.ClientPlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof AxeItem
                || player.getOffHandStack().getItem() instanceof AxeItem;
    }
}
