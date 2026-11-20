package com.bodycraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BodyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("bodycraft.json");

    public static BodyConfig INSTANCE = new BodyConfig();

    public GenderPreset gender = GenderPreset.FEMALE;

    // Торс и бёдра
    public float chestSize = 0.85F;      // 0.0 .. 1.5
    public float chestPitch = 0.35F;     // наклон груди (0.15 .. 0.65)
    public float waistWidth = 0.80F;     // 0.65 .. 1.25
    public float hipWidth = 1.15F;       // 0.85 .. 1.45
    public float thighSize = 1.25F;      // ляшки (0.85 .. 1.60)

    // Конечности
    public float armThickness = 0.85F;   // 0.65 .. 1.35
    public float armLength = 1.00F;      // 0.80 .. 1.20
    public float legThickness = 0.95F;   // 0.70 .. 1.35
    public float legLength = 1.00F;      // 0.80 .. 1.20

    // 3D-одежда
    public TopStyle topStyle = TopStyle.CROP_TOP;
    public BottomStyle bottomStyle = BottomStyle.MINI_SKIRT;
    public int topColorIndex = 0;
    public int bottomColorIndex = 1;

    public static final int[] PALETTE = {
        0xFFE63946, // Красный
        0xFF1D3557, // Тёмно-синий
        0xFF2B2D42, // Графит
        0xFFF1FAEE, // Молочный
        0xFFFF9F1C, // Янтарный
        0xFF8338EC, // Фиолетовый
        0xFFFF66B2, // Розовый
        0xFF2A9D8F  // Изумрудный
    };

    public enum GenderPreset {
        FEMALE("Женский"),
        MALE("Мужской"),
        VANILLA("Ванила");

        public final String label;
        GenderPreset(String label) { this.label = label; }
    }

    public enum TopStyle {
        NONE("Нет (скин)"),
        CROP_TOP("Кроп-топ"),
        HOODIE("Худи"),
        JACKET("Куртка");

        public final String label;
        TopStyle(String label) { this.label = label; }
        public TopStyle next() {
            TopStyle[] v = values();
            return v[(ordinal() + 1) % v.length];
        }
    }

    public enum BottomStyle {
        NONE("Нет (скин)"),
        SHORTS("Шорты"),
        MINI_SKIRT("Мини-юбка"),
        WIDE_PANTS("Широкие штаны");

        public final String label;
        BottomStyle(String label) { this.label = label; }
        public BottomStyle next() {
            BottomStyle[] v = values();
            return v[(ordinal() + 1) % v.length];
        }
    }

    public void applyPreset(GenderPreset preset) {
        this.gender = preset;
        switch (preset) {
            case FEMALE -> {
                chestSize = 0.85F;
                chestPitch = 0.35F;
                waistWidth = 0.78F;
                hipWidth = 1.18F;
                thighSize = 1.28F;
                armThickness = 0.82F;
                armLength = 0.98F;
                legThickness = 0.94F;
                legLength = 1.02F;
            }
            case MALE -> {
                chestSize = 0.0F;
                chestPitch = 0.25F;
                waistWidth = 1.06F;
                hipWidth = 0.98F;
                thighSize = 1.02F;
                armThickness = 1.10F;
                armLength = 1.02F;
                legThickness = 1.05F;
                legLength = 1.00F;
            }
            case VANILLA -> {
                chestSize = 0.0F;
                chestPitch = 0.35F;
                waistWidth = 1.00F;
                hipWidth = 1.00F;
                thighSize = 1.00F;
                armThickness = 1.00F;
                armLength = 1.00F;
                legThickness = 1.00F;
                legLength = 1.00F;
                topStyle = TopStyle.NONE;
                bottomStyle = BottomStyle.NONE;
            }
        }
    }

    public int getTopColor() {
        return PALETTE[Math.floorMod(topColorIndex, PALETTE.length)];
    }

    public int getBottomColor() {
        return PALETTE[Math.floorMod(bottomColorIndex, PALETTE.length)];
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                BodyConfig loaded = GSON.fromJson(Files.readString(CONFIG_PATH), BodyConfig.class);
                if (loaded != null) INSTANCE = loaded;
            } catch (IOException ignored) {
                INSTANCE = new BodyConfig();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (IOException ignored) {
        }
    }
}
