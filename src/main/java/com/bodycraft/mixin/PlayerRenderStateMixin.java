package com.bodycraft.mixin;

import com.bodycraft.render.PlayerRenderStateExt;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements PlayerRenderStateExt {
    @Unique
    private boolean bodycraft$localPlayer = false;

    @Override
    public boolean bodycraft$isLocalPlayer() {
        return this.bodycraft$localPlayer;
    }

    @Override
    public void bodycraft$setLocalPlayer(boolean value) {
        this.bodycraft$localPlayer = value;
    }
}
