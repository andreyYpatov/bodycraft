package com.bodycraft.mixin;

import com.bodycraft.config.BodyConfig;
import com.bodycraft.render.PlayerRenderStateExt;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin extends HumanoidModel<PlayerRenderState> {

    protected PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At("TAIL"))
    private void bodycraft$applyProportions(PlayerRenderState state, CallbackInfo ci) {
        resetScale(this.body);
        resetScale(this.leftArm);
        resetScale(this.rightArm);
        resetScale(this.leftLeg);
        resetScale(this.rightLeg);

        if (!(state instanceof PlayerRenderStateExt ext) || !ext.bodycraft$isLocalPlayer()) {
            return;
        }

        BodyConfig cfg = BodyConfig.INSTANCE;

        // 1. Талия / торс
        this.body.xScale = cfg.waistWidth;
        this.body.zScale = 0.85F + (cfg.waistWidth - 0.85F) * 0.65F;

        // 2. Руки + компенсация стыка с плечами
        this.leftArm.xScale = cfg.armThickness;
        this.leftArm.zScale = cfg.armThickness;
        this.leftArm.yScale = cfg.armLength;

        this.rightArm.xScale = cfg.armThickness;
        this.rightArm.zScale = cfg.armThickness;
        this.rightArm.yScale = cfg.armLength;

        float shoulderOffset = 4.0F * cfg.waistWidth + 1.0F * cfg.armThickness;
        this.leftArm.x = shoulderOffset;
        this.rightArm.x = -shoulderOffset;

        // 3. Ноги + компенсация уровня земли
        this.leftLeg.xScale = cfg.legThickness;
        this.leftLeg.zScale = cfg.legThickness;
        this.leftLeg.yScale = cfg.legLength;

        this.rightLeg.xScale = cfg.legThickness;
        this.rightLeg.zScale = cfg.legThickness;
        this.rightLeg.yScale = cfg.legLength;

        float legSpacing = 1.9F * Math.max(0.85F, cfg.hipWidth);
        this.leftLeg.x = legSpacing;
        this.rightLeg.x = -legSpacing;

        float verticalShift = 12.0F * (1.0F - cfg.legLength);
        this.head.y += verticalShift;
        this.body.y += verticalShift;
        this.leftArm.y += verticalShift;
        this.rightArm.y += verticalShift;
        this.leftLeg.y += verticalShift;
        this.rightLeg.y += verticalShift;
    }

    private static void resetScale(ModelPart part) {
        part.xScale = 1.0F;
        part.yScale = 1.0F;
        part.zScale = 1.0F;
    }
}
