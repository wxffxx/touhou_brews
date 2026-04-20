package com.wxffxx.touhoubrews.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages loading, saving, and hot-reloading of all Touhou Brews config files.
 */
public class BrewConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("touhou_brews/config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private static final String EFFECTS_FILE   = "brew_effects.json";
    private static final String ALGORITHM_FILE = "quality_algorithm.json";
    private static final String RECIPES_FILE   = "brew_recipes.json";
    private static final String DISPLAY_FILE   = "brew_display.json";

    private static BrewEffectsConfig      effectsConfig   = buildDefaultEffects();
    private static QualityAlgorithmConfig algorithmConfig = new QualityAlgorithmConfig();
    private static BrewRecipesConfig      recipesConfig   = buildDefaultRecipes();
    private static BrewDisplayConfig      displayConfig   = buildDefaultDisplay();

    public static BrewEffectsConfig      effects()   { return effectsConfig; }
    public static QualityAlgorithmConfig algorithm() { return algorithmConfig; }
    public static BrewRecipesConfig      recipes()   { return recipesConfig; }
    public static BrewDisplayConfig      display()   { return displayConfig; }

    private static Path configDir() {
        return FabricLoader.getInstance().getConfigDir().resolve("touhou_brews");
    }

    /** Load all config files. Creates defaults if missing. Returns true on full success. */
    public static boolean load() {
        try {
            Files.createDirectories(configDir());
            effectsConfig   = loadOrCreate(EFFECTS_FILE,   BrewEffectsConfig.class,      buildDefaultEffects());
            algorithmConfig = loadOrCreate(ALGORITHM_FILE, QualityAlgorithmConfig.class,  new QualityAlgorithmConfig());
            recipesConfig   = loadOrCreate(RECIPES_FILE,   BrewRecipesConfig.class,       buildDefaultRecipes());
            displayConfig   = loadOrCreate(DISPLAY_FILE,   BrewDisplayConfig.class,       buildDefaultDisplay());
            LOGGER.info("[TouhouBrews] All configs loaded successfully.");
            return true;
        } catch (Exception e) {
            LOGGER.error("[TouhouBrews] Failed to load config: {}", e.getMessage());
            return false;
        }
    }

    private static <T> T loadOrCreate(String filename, Class<T> clazz, T defaults) throws IOException {
        Path file = configDir().resolve(filename);
        if (!Files.exists(file)) {
            try (Writer w = Files.newBufferedWriter(file)) {
                GSON.toJson(defaults, w);
                LOGGER.info("[TouhouBrews] Created default config: {}", filename);
            }
            return defaults;
        }
        try (Reader r = Files.newBufferedReader(file)) {
            T loaded = GSON.fromJson(r, clazz);
            return loaded != null ? loaded : defaults;
        }
    }

    // =========================================================
    //  DEFAULT RECIPES
    // =========================================================

    public static BrewRecipesConfig buildDefaultRecipes() {
        BrewRecipesConfig cfg = new BrewRecipesConfig();

        cfg.fermentation_barrel = new ArrayList<>(List.of(
            barrel("ibuki_sake",   "touhou_brews:koji_rice",   "touhou_brews:steamed_rice", "water_bottle", true,  1200, "ibuki_sake"),
            barrel("remilia_wine", "touhou_brews:grape_juice", null,                         "water_bottle", false, 900,  "remilia_wine"),
            barrel("beer",         "touhou_brews:wort",        "touhou_brews:hops",          "water_bottle", true,  800,  "beer"),
            barrel("mijiu",        "touhou_brews:steamed_rice",null,                         "water_bottle", false, 700,  "mijiu"),
            barrel("huangjiu",     "touhou_brews:koji_rice",   "minecraft:sugar",            "water_bottle", true,  1000, "huangjiu"),
            barrel("mead",         "minecraft:honey_bottle",   "minecraft:sugar",            "water_bottle", true,  900,  "mead")
        ));

        cfg.infusion_jar = new ArrayList<>(List.of(
            infusion("eirin_umeshu", "touhou_brews:ibuki_sake",   "touhou_brews:green_plum", "minecraft:sugar", 3, "eirin_umeshu"),
            infusion("aomeshu",      "touhou_brews:grape_juice",  "touhou_brews:green_plum", "minecraft:sugar", 3, "aomeshu")
        ));

        return cfg;
    }

    private static BrewRecipeEntry barrel(String id, String slot0, String slot1, String slot2,
                                          boolean req1, int time, String out) {
        BrewRecipeEntry e = new BrewRecipeEntry();
        e.id = id; e.slot0 = slot0; e.slot1 = slot1; e.slot2 = slot2;
        e.require_slot1 = req1; e.perfect_time_ticks = time; e.output_brew_type = out;
        return e;
    }

    private static BrewRecipeEntry infusion(String id, String base, String fruit, String sweetener,
                                            int quality, String out) {
        BrewRecipeEntry e = new BrewRecipeEntry();
        e.id = id; e.base = base; e.fruit = fruit; e.sweetener = sweetener;
        e.output_quality = quality; e.output_brew_type = out;
        return e;
    }

    // =========================================================
    //  DEFAULT DISPLAY
    // =========================================================

    public static BrewDisplayConfig buildDefaultDisplay() {
        BrewDisplayConfig cfg = new BrewDisplayConfig();
        for (int i = 1; i <= 5; i++) {
            Map<String, String> names = new LinkedHashMap<>();
            names.put("0", "失败品 #" + i);
            names.put("1", "初级自定义酒 #" + i);
            names.put("2", "廉价自定义酒 #" + i);
            names.put("3", "普通自定义酒 #" + i);
            names.put("4", "上品自定义酒 #" + i);
            names.put("5", "极品自定义酒 #" + i);
            cfg.brews.put("custom_" + i,
                new BrewDisplayEntry("自定义酒类 #" + i + "。在 brew_display.json 中修改描述。", names));
        }
        return cfg;
    }

    // =========================================================
    //  DEFAULT EFFECTS
    // =========================================================

    public static BrewEffectsConfig buildDefaultEffects() {
        BrewEffectsConfig cfg = new BrewEffectsConfig();
        cfg.brews = new LinkedHashMap<>();

        final String STR  = "minecraft:strength";
        final String RES  = "minecraft:resistance";
        final String NV   = "minecraft:night_vision";
        final String REG  = "minecraft:regeneration";
        final String SPD  = "minecraft:speed";
        final String HST  = "minecraft:haste";
        final String LCK  = "minecraft:luck";
        final String ABS  = "minecraft:absorption";
        final String FIRE = "minecraft:fire_resistance";
        final String NAU  = "minecraft:nausea";

        cfg.brews.put("ibuki_sake", qm(
            lof(), lof(e(NAU,210,0)),
            lof(e(STR,480,0),e(NAU,180,0)),
            lof(e(STR,720,0),e(RES,720,0),e(NAU,150,0)),
            lof(e(STR,960,1),e(RES,960,1)),
            lof(e(STR,36000,2),e(RES,36000,1)),
            lof(e(STR,36000,3),e(RES,36000,2))));

        cfg.brews.put("remilia_wine", qm(
            lof(), lof(e(NV,1200,0)),
            lof(e(NV,1680,0),e(STR,240,0)),
            lof(e(NV,2160,0),e(STR,360,0)),
            lof(e(NV,2400,0),e(STR,480,0),e(REG,120,0)),
            lof(e(NV,36000,0),e(STR,600,1),e(REG,200,1)),
            lof(e(NV,36000,0),e(STR,800,2),e(REG,300,2))));

        cfg.brews.put("eirin_umeshu", qm(
            lof(), lof(e(REG,140,0),e(SPD,560,0),e(NAU,105,0)),
            lof(e(REG,200,0),e(SPD,800,0),e(NAU,90,0)),
            lof(e(REG,280,0),e(SPD,1080,0),e(HST,1080,0)),
            lof(e(REG,360,0),e(SPD,1320,0),e(HST,1320,0),e(LCK,1320,0)),
            lof(e(REG,36000,1),e(SPD,36000,0),e(HST,36000,0),e(LCK,36000,0)),
            lof(e(REG,36000,2),e(SPD,36000,1),e(HST,36000,1),e(LCK,36000,1))));

        cfg.brews.put("aomeshu", qm(
            lof(), lof(e(REG,70,0),e(SPD,280,0),e(NAU,105,0)),
            lof(e(REG,100,0),e(SPD,400,0),e(NAU,90,0)),
            lof(e(REG,140,0),e(SPD,560,0),e(NAU,75,0)),
            lof(e(REG,180,0),e(SPD,720,0),e(NAU,60,0)),
            lof(e(REG,36000,0),e(SPD,36000,0)),
            lof(e(REG,36000,1),e(SPD,36000,1))));

        cfg.brews.put("beer", qm(
            lof(), lof(e(RES,315,0),e(HST,315,0),e(NAU,140,0)),
            lof(e(RES,450,0),e(HST,450,0),e(NAU,120,0)),
            lof(e(RES,630,0),e(HST,630,1),e(NAU,100,0)),
            lof(e(RES,810,1),e(HST,810,1)),
            lof(e(RES,36000,1),e(HST,36000,1)),
            lof(e(RES,36000,2),e(HST,36000,2),e(STR,36000,1))));

        cfg.brews.put("mijiu", qm(
            lof(), lof(e(SPD,315,0),e(NAU,126,0)),
            lof(e(SPD,450,0),e(NAU,108,0)),
            lof(e(SPD,630,0),e(NAU,90,0)),
            lof(e(SPD,810,0),e(NAU,72,0)),
            lof(e(SPD,36000,1)),
            lof(e(SPD,36000,2),e(HST,36000,0))));

        cfg.brews.put("huangjiu", qm(
            lof(), lof(e(REG,87,0),e(RES,315,0)),
            lof(e(REG,125,0),e(RES,450,0)),
            lof(e(REG,175,0),e(RES,630,0)),
            lof(e(REG,225,1),e(RES,810,0)),
            lof(e(REG,36000,1),e(RES,36000,0)),
            lof(e(REG,36000,2),e(RES,36000,1))));

        cfg.brews.put("mead", qm(
            lof(), lof(e(REG,105,0),e(ABS,315,0)),
            lof(e(REG,150,0),e(ABS,450,0)),
            lof(e(REG,210,0),e(ABS,630,0)),
            lof(e(REG,270,0),e(ABS,810,1)),
            lof(e(REG,36000,0),e(ABS,36000,1)),
            lof(e(REG,36000,1),e(ABS,36000,2))));

        cfg.brews.put("baijiu", qm(
            lof(), lof(e(STR,280,0),e(FIRE,140,0),e(NAU,224,0)),
            lof(e(STR,400,0),e(FIRE,200,0),e(NAU,192,0)),
            lof(e(STR,560,0),e(FIRE,280,0),e(NAU,160,0)),
            lof(e(STR,720,1),e(FIRE,360,0),e(NAU,128,0)),
            lof(e(STR,36000,1),e(FIRE,36000,0)),
            lof(e(STR,36000,2),e(FIRE,36000,0),e(RES,36000,0))));

        // Empty placeholders for custom_1 ~ custom_5
        for (int i = 1; i <= 5; i++) {
            cfg.brews.put("custom_" + i, qm(
                lof(),lof(),lof(),lof(),lof(),lof(),lof()));
        }

        return cfg;
    }

    // ---- helpers ----

    private static BrewEffectEntry e(String effect, int duration, int amplifier) {
        return new BrewEffectEntry(effect, duration, amplifier);
    }

    @SafeVarargs
    private static Map<String, List<BrewEffectEntry>> qm(List<BrewEffectEntry>... byQuality) {
        Map<String, List<BrewEffectEntry>> map = new LinkedHashMap<>();
        for (int i = 0; i < byQuality.length; i++) map.put("quality_" + i, byQuality[i]);
        return map;
    }

    @SafeVarargs
    private static <T> List<T> lof(T... items) {
        return items.length == 0 ? new ArrayList<>() : new ArrayList<>(Arrays.asList(items));
    }
}
