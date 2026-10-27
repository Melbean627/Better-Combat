package dev.melbean627.offhandswordlunge;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.SwordItem;

public final class OffhandSwordLungeClient implements ClientModInitializer {
    private boolean useKeyWasPressed;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean useKeyPressed = client.options.useKey.isPressed();
            if (useKeyPressed
                    && !useKeyWasPressed
                    && client.currentScreen == null
                    && client.player != null
                    && !client.player.isSpectator()
                    && client.player.getOffHandStack().getItem() instanceof SwordItem) {
                OffhandSwordLunge.applyLunge(client.player);
                ClientPlayNetworking.send(new LungePayload());
            }
            useKeyWasPressed = useKeyPressed;
        });
    }
}
