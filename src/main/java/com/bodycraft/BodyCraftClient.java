package com.bodycraft;

import com.bodycraft.config.BodyConfig;
import com.bodycraft.gui.BodyCustomizerScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class BodyCraftClient implements ClientModInitializer {
    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        BodyConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.bodycraft.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.bodycraft"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new BodyCustomizerScreen(null));
                }
            }
        });
    }
}
