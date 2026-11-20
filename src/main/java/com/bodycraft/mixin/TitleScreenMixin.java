package com.bodycraft.mixin;

import com.bodycraft.gui.BodyCustomizerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void bodycraft$addTitleButton(CallbackInfo ci) {
        this.addRenderableWidget(Button.builder(
                Component.literal("Персонаж"),
                btn -> this.minecraft.setScreen(new BodyCustomizerScreen(this))
        ).bounds(this.width / 2 + 104, this.height / 4 + 48, 84, 20).build());
    }
}
