package com.bodycraft.mixin;

import com.bodycraft.gui.BodyCustomizerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) { super(title); }

    @Inject(method = "createPauseMenu", at = @At("TAIL"))
    private void bodycraft$addCustomizeButton(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(
                Component.literal("Персонаж"),
                btn -> this.minecraft.setScreen(new BodyCustomizerScreen(this))
        ).bounds(this.width / 2 + 106, this.height / 4 + 24, 82, 20).build());
    }
}
