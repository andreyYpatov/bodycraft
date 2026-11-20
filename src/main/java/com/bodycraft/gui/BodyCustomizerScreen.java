package com.bodycraft.gui;

import com.bodycraft.config.BodyConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class BodyCustomizerScreen extends Screen {
    private final Screen parent;
    private int activeTab = 0; // 0 = Фигура, 1 = Конечности, 2 = Одежда

    public BodyCustomizerScreen(Screen parent) {
        super(Component.literal("Редактор персонажа"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        BodyConfig cfg = BodyConfig.INSTANCE;

        int leftX = 20;
        int panelW = Math.min(230, this.width / 2 - 20);
        int y = 32;

        // Вкладки
        int tabW = (panelW - 8) / 3;
        addRenderableWidget(Button.builder(Component.literal("Фигура"), b -> switchTab(0))
                .bounds(leftX, y, tabW, 20).build()).active = (activeTab != 0);
        addRenderableWidget(Button.builder(Component.literal("Руки/Ноги"), b -> switchTab(1))
                .bounds(leftX + tabW + 4, y, tabW, 20).build()).active = (activeTab != 1);
        addRenderableWidget(Button.builder(Component.literal("Одежда"), b -> switchTab(2))
                .bounds(leftX + (tabW + 4) * 2, y, tabW, 20).build()).active = (activeTab != 2);

        y += 28;

        // Пресеты пола
        int presetW = (panelW - 8) / 3;
        for (int i = 0; i < BodyConfig.GenderPreset.values().length; i++) {
            BodyConfig.GenderPreset preset = BodyConfig.GenderPreset.values()[i];
            addRenderableWidget(Button.builder(Component.literal(preset.label), b -> {
                cfg.applyPreset(preset);
                this.init();
            }).bounds(leftX + i * (presetW + 4), y, presetW, 18).build());
        }

        y += 26;

        // Контент активной вкладки
        if (activeTab == 0) {
            addSlider(leftX, y, panelW, "Грудь", cfg.chestSize, 0.0F, 1.5F, v -> cfg.chestSize = v);
            y += 24;
            addSlider(leftX, y, panelW, "Наклон груди", cfg.chestPitch, 0.15F, 0.65F, v -> cfg.chestPitch = v);
            y += 24;
            addSlider(leftX, y, panelW, "Талия", cfg.waistWidth, 0.65F, 1.25F, v -> cfg.waistWidth = v);
            y += 24;
            addSlider(leftX, y, panelW, "Ширина бёдер", cfg.hipWidth, 0.85F, 1.45F, v -> cfg.hipWidth = v);
            y += 24;
            addSlider(leftX, y, panelW, "Ляшки", cfg.thighSize, 0.85F, 1.60F, v -> cfg.thighSize = v);
        } else if (activeTab == 1) {
            addSlider(leftX, y, panelW, "Толщина рук", cfg.armThickness, 0.65F, 1.35F, v -> cfg.armThickness = v);
            y += 24;
            addSlider(leftX, y, panelW, "Длина рук", cfg.armLength, 0.80F, 1.20F, v -> cfg.armLength = v);
            y += 24;
            addSlider(leftX, y, panelW, "Толщина ног", cfg.legThickness, 0.70F, 1.35F, v -> cfg.legThickness = v);
            y += 24;
            addSlider(leftX, y, panelW, "Длина ног", cfg.legLength, 0.80F, 1.20F, v -> cfg.legLength = v);
        } else {
            addRenderableWidget(Button.builder(
                    Component.literal("Верх: " + cfg.topStyle.label),
                    b -> { cfg.topStyle = cfg.topStyle.next(); this.init(); }
            ).bounds(leftX, y, panelW, 20).build());
            y += 24;

            addRenderableWidget(Button.builder(
                    Component.literal("Цвет верха #" + (cfg.topColorIndex + 1)),
                    b -> { cfg.topColorIndex = (cfg.topColorIndex + 1) % BodyConfig.PALETTE.length; this.init(); }
            ).bounds(leftX, y, panelW, 20).build());
            y += 28;

            addRenderableWidget(Button.builder(
                    Component.literal("Низ: " + cfg.bottomStyle.label),
                    b -> { cfg.bottomStyle = cfg.bottomStyle.next(); this.init(); }
            ).bounds(leftX, y, panelW, 20).build());
            y += 24;

            addRenderableWidget(Button.builder(
                    Component.literal("Цвет низа #" + (cfg.bottomColorIndex + 1)),
                    b -> { cfg.bottomColorIndex = (cfg.bottomColorIndex + 1) % BodyConfig.PALETTE.length; this.init(); }
            ).bounds(leftX, y, panelW, 20).build());
        }

        // Нижние кнопки
        int bottomY = this.height - 30;
        addRenderableWidget(Button.builder(Component.literal("Сброс"), b -> {
            cfg.applyPreset(BodyConfig.GenderPreset.VANILLA);
            this.init();
        }).bounds(leftX, bottomY, 80, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Сохранить и выйти"), b -> {
            BodyConfig.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(leftX + 86, bottomY, panelW - 86, 20).build());
    }

    private void switchTab(int tab) {
        this.activeTab = tab;
        this.init();
    }

    private void addSlider(int x, int y, int width, String label, float initial, float min, float max, Consumer<Float> setter) {
        double normalized = (initial - min) / (max - min);
        addRenderableWidget(new AbstractSliderButton(x, y, width, 20, Component.empty(), normalized) {
            { updateMessage(); }

            @Override
            protected void updateMessage() {
                float actual = (float) (min + this.value * (max - min));
                this.setMessage(Component.literal(String.format("%s: %.0f%%", label, actual * 100.0F)));
            }

            @Override
            protected void applyValue() {
                float actual = (float) (min + this.value * (max - min));
                setter.accept(actual);
            }
        });
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(this.font, this.title, 20, 12, 0xFFFFFF);

        int previewLeft = this.width / 2 + 20;
        int previewRight = this.width - 20;
        int previewTop = 32;
        int previewBottom = this.height - 32;

        graphics.fill(previewLeft, previewTop, previewRight, previewBottom, 0x66000000);

        if (this.minecraft != null && this.minecraft.player != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    previewLeft, previewTop, previewRight, previewBottom,
                    72, 0.0625F,
                    mouseX, mouseY,
                    this.minecraft.player
            );
        } else {
            graphics.drawCenteredString(
                    this.font,
                    "Зайди в мир для 3D-превью",
                    (previewLeft + previewRight) / 2,
                    (previewTop + previewBottom) / 2,
                    0xCCCCCC
            );
        }

        if (activeTab == 2) {
            BodyConfig cfg = BodyConfig.INSTANCE;
            graphics.fill(previewLeft + 8, previewTop + 8, previewLeft + 24, previewTop + 24, cfg.getTopColor());
            graphics.fill(previewLeft + 30, previewTop + 8, previewLeft + 46, previewTop + 24, cfg.getBottomColor());
        }
    }

    @Override
    public void onClose() {
        BodyConfig.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }
}
