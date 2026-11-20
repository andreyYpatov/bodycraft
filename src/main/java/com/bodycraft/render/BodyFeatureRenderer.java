package com.bodycraft.render;

import com.bodycraft.config.BodyConfig;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class BodyFeatureRenderer extends RenderLayer<PlayerRenderState, PlayerModel> {

    private static ResourceLocation whiteClothTex;

    // Части анатомии (используют UV текущего скина игрока)
    private final ModelPart bustPart;
    private final ModelPart pelvisPart;
    private final ModelPart rightThighPart;
    private final ModelPart leftThighPart;

    // Части 3D-одежды
    private final ModelPart cropTopPart;
    private final ModelPart hoodieTorsoPart;
    private final ModelPart hoodieHoodPart;
    private final ModelPart jacketPart;
    private final ModelPart sleevePart;
    private final ModelPart shortsPart;
    private final ModelPart skirtFront, skirtBack, skirtLeft, skirtRight;
    private final ModelPart widePantLegPart;

    public BodyFeatureRenderer(RenderLayerParent<PlayerRenderState, PlayerModel> parent) {
        super(parent);

        // 1. Грудь: UV (20, 21) берёт пиксели прямо с передней части груди скина
        this.bustPart = bakeSingle(64, 64, root -> root.addOrReplaceChild("bust",
                CubeListBuilder.create()
                        .texOffs(20, 21)
                        .addBox(-3.6F, 0.0F, -3.0F, 7.2F, 3.8F, 3.2F, new CubeDeformation(-0.02F)),
                PartPose.offset(0.0F, 1.4F, -1.9F)));

        // 2. Таз / бёдра: UV (16, 26) с нижней части торса скина
        this.pelvisPart = bakeSingle(64, 64, root -> root.addOrReplaceChild("pelvis",
                CubeListBuilder.create()
                        .texOffs(16, 26)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 3.4F, 4.0F, new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, 8.8F, 0.0F)));

        // 3. Верхняя часть ног (ляшки): UV берётся с правой и левой ноги скина
        this.rightThighPart = bakeSingle(64, 64, root -> root.addOrReplaceChild("r_thigh",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.2F, 4.0F, new CubeDeformation(0.08F)),
                PartPose.ZERO));

        this.leftThighPart = bakeSingle(64, 64, root -> root.addOrReplaceChild("l_thigh",
                CubeListBuilder.create()
                        .texOffs(16, 48)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.2F, 4.0F, new CubeDeformation(0.08F)),
                PartPose.ZERO));

        // 4. Одежда — верх
        this.cropTopPart = bakeSingle(16, 16, root -> root.addOrReplaceChild("crop_top",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.2F, 4.0F, new CubeDeformation(0.35F)),
                PartPose.ZERO));

        this.hoodieTorsoPart = bakeSingle(16, 16, root -> root.addOrReplaceChild("hoodie",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.2F, 4.0F, new CubeDeformation(0.55F)),
                PartPose.ZERO));

        this.hoodieHoodPart = bakeSingle(16, 16, root -> root.addOrReplaceChild("hood",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.2F, -1.5F, 1.5F, 8.4F, 4.0F, 3.2F),
                PartPose.ZERO));

        this.jacketPart = bakeSingle(16, 16, root -> {
            PartDefinition j = root.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.ZERO);
            j.addOrReplaceChild("left_side", CubeListBuilder.create()
                    .addBox(0.8F, 0.0F, -2.2F, 3.4F, 11.5F, 4.4F, new CubeDeformation(0.32F)), PartPose.ZERO);
            j.addOrReplaceChild("right_side", CubeListBuilder.create()
                    .addBox(-4.2F, 0.0F, -2.2F, 3.4F, 11.5F, 4.4F, new CubeDeformation(0.32F)), PartPose.ZERO);
            j.addOrReplaceChild("back", CubeListBuilder.create()
                    .addBox(-4.0F, 0.0F, 1.6F, 8.0F, 11.5F, 0.8F, new CubeDeformation(0.32F)), PartPose.ZERO);
        });

        this.sleevePart = bakeSingle(16, 16, root -> root.addOrReplaceChild("sleeve",
                CubeListBuilder.create()
                        .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 10.5F, 4.0F, new CubeDeformation(0.45F)),
                PartPose.ZERO));

        // 5. Одежда — низ
        this.shortsPart = bakeSingle(16, 16, root -> root.addOrReplaceChild("shorts",
                CubeListBuilder.create()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.8F, 4.0F, new CubeDeformation(0.36F)),
                PartPose.ZERO));

        this.widePantLegPart = bakeSingle(16, 16, root -> root.addOrReplaceChild("wide_leg",
                CubeListBuilder.create()
                        .addBox(-2.2F, 0.0F, -2.2F, 4.4F, 11.5F, 4.4F, new CubeDeformation(0.35F)),
                PartPose.ZERO));

        this.skirtFront = bakeSingle(16, 16, root -> root.addOrReplaceChild("sf",
                CubeListBuilder.create().addBox(-4.4F, 0.0F, -0.5F, 8.8F, 5.2F, 1.0F),
                PartPose.offset(0.0F, 10.2F, -2.2F)));
        this.skirtBack = bakeSingle(16, 16, root -> root.addOrReplaceChild("sb",
                CubeListBuilder.create().addBox(-4.4F, 0.0F, -0.5F, 8.8F, 5.2F, 1.0F),
                PartPose.offset(0.0F, 10.2F, 2.2F)));
        this.skirtLeft = bakeSingle(16, 16, root -> root.addOrReplaceChild("sl",
                CubeListBuilder.create().addBox(-0.5F, 0.0F, -2.4F, 1.0F, 5.2F, 4.8F),
                PartPose.offset(4.2F, 10.2F, 0.0F)));
        this.skirtRight = bakeSingle(16, 16, root -> root.addOrReplaceChild("sr",
                CubeListBuilder.create().addBox(-0.5F, 0.0F, -2.4F, 1.0F, 5.2F, 4.8F),
                PartPose.offset(-4.2F, 10.2F, 0.0F)));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PlayerRenderState state, float yRot, float xRot) {
        if (!(state instanceof PlayerRenderStateExt ext) || !ext.bodycraft$isLocalPlayer()) {
            return;
        }

        BodyConfig cfg = BodyConfig.INSTANCE;
        PlayerModel model = this.getParentModel();
        ResourceLocation skinTex = state.skin.texture();
        VertexConsumer skinConsumer = buffer.getBuffer(RenderType.entityTranslucent(skinTex));

        // --- АНАТОМИЯ: ГРУДЬ И ТАЗ ---
        poseStack.pushPose();
        model.body.translateAndRotate(poseStack);

        if (cfg.chestSize > 0.02F) {
            poseStack.pushPose();
            poseStack.scale(1.0F / cfg.waistWidth, 1.0F, 1.0F);
            bustPart.xRot = cfg.chestPitch;
            bustPart.xScale = 0.88F + cfg.chestSize * 0.14F;
            bustPart.yScale = 0.72F + cfg.chestSize * 0.32F;
            bustPart.zScale = cfg.chestSize;
            bustPart.render(poseStack, skinConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        if (Math.abs(cfg.hipWidth - cfg.waistWidth) > 0.03F) {
            poseStack.pushPose();
            float hipRelX = cfg.hipWidth / cfg.waistWidth;
            pelvisPart.xScale = hipRelX;
            pelvisPart.zScale = 0.92F + (cfg.hipWidth - 1.0F) * 0.65F;
            pelvisPart.render(poseStack, skinConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        // --- 3D-ОДЕЖДА (ВЕРХ И ЮБКА) ---
        VertexConsumer clothConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getWhiteClothTexture()));
        int topCol = cfg.getTopColor();
        int botCol = cfg.getBottomColor();

        switch (cfg.topStyle) {
            case CROP_TOP -> {
                cropTopPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol);
                if (cfg.chestSize > 0.02F) {
                    poseStack.pushPose();
                    poseStack.scale(1.04F / cfg.waistWidth, 1.04F, 1.08F);
                    bustPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol);
                    poseStack.popPose();
                }
            }
            case HOODIE -> {
                hoodieTorsoPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol);
                hoodieHoodPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol);
            }
            case JACKET -> jacketPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol);
            case NONE -> {}
        }

        if (cfg.bottomStyle == BodyConfig.BottomStyle.MINI_SKIRT) {
            poseStack.pushPose();
            float skirtScale = Math.max(cfg.hipWidth, cfg.thighSize * 0.92F) / cfg.waistWidth;
            poseStack.scale(skirtScale, 1.0F, skirtScale);

            float legSwing = Math.max(Math.abs(model.leftLeg.xRot), Math.abs(model.rightLeg.xRot)) * 0.45F;
            skirtFront.xRot = -0.26F - legSwing;
            skirtBack.xRot = 0.26F + legSwing;
            skirtLeft.zRot = -0.24F;
            skirtRight.zRot = 0.24F;

            skirtFront.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, botCol);
            skirtBack.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, botCol);
            skirtLeft.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, botCol);
            skirtRight.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, botCol);
            poseStack.popPose();
        }
        poseStack.popPose();

        // --- РУКАВА ДЛЯ ХУДИ И КУРТКИ ---
        if (cfg.topStyle == BodyConfig.TopStyle.HOODIE || cfg.topStyle == BodyConfig.TopStyle.JACKET) {
            renderOnLimb(poseStack, model.leftArm, () ->
                    sleevePart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol));
            renderOnLimb(poseStack, model.rightArm, () ->
                    sleevePart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, topCol));
        }

        // --- АНАТОМИЯ: ЛЯШКИ + НИЖНЯЯ ОДЕЖДА ---
        float thighRel = cfg.thighSize / Math.max(0.5F, cfg.legThickness);
        renderLegAttachments(poseStack, model.rightLeg, rightThighPart, thighRel, cfg, skinConsumer, clothConsumer, packedLight, botCol);
        renderLegAttachments(poseStack, model.leftLeg, leftThighPart, thighRel, cfg, skinConsumer, clothConsumer, packedLight, botCol);
    }

    private void renderLegAttachments(PoseStack poseStack, ModelPart legBone, ModelPart thighPart,
                                      float thighRel, BodyConfig cfg,
                                      VertexConsumer skinConsumer, VertexConsumer clothConsumer,
                                      int packedLight, int botCol) {
        poseStack.pushPose();
        legBone.translateAndRotate(poseStack);

        if (thighRel > 1.02F) {
            thighPart.xScale = thighRel;
            thighPart.zScale = thighRel;
            thighPart.render(poseStack, skinConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (cfg.bottomStyle == BodyConfig.BottomStyle.SHORTS) {
            shortsPart.xScale = Math.max(1.0F, thighRel);
            shortsPart.zScale = Math.max(1.0F, thighRel);
            shortsPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, botCol);
        } else if (cfg.bottomStyle == BodyConfig.BottomStyle.WIDE_PANTS) {
            widePantLegPart.xScale = Math.max(1.05F, thighRel);
            widePantLegPart.zScale = Math.max(1.05F, thighRel);
            widePantLegPart.render(poseStack, clothConsumer, packedLight, OverlayTexture.NO_OVERLAY, botCol);
        }

        poseStack.popPose();
    }

    private void renderOnLimb(PoseStack poseStack, ModelPart bone, Runnable action) {
        poseStack.pushPose();
        bone.translateAndRotate(poseStack);
        action.run();
        poseStack.popPose();
    }

    private static ModelPart bakeSingle(int texW, int texH, Consumer<PartDefinition> builder) {
        MeshDefinition mesh = new MeshDefinition();
        builder.accept(mesh.getRoot());
        return LayerDefinition.create(mesh, texW, texH).bakeRoot();
    }

    private static ResourceLocation getWhiteClothTexture() {
        if (whiteClothTex == null) {
            NativeImage img = new NativeImage(16, 16, false);
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    int shade = ((x + y) % 2 == 0) ? 0xFFF5F5F5 : 0xFFFFFFFF;
                    img.setPixelABGR(x, y, shade);
                }
            }
            DynamicTexture tex = new DynamicTexture(img);
            whiteClothTex = ResourceLocation.fromNamespaceAndPath("bodycraft", "dynamic/cloth_base");
            Minecraft.getInstance().getTextureManager().register(whiteClothTex, tex);
        }
        return whiteClothTex;
    }
}
