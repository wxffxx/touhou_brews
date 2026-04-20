package com.wxffxx.touhoubrews.config;

/**
 * A single fermentation or infusion recipe entry.
 */
public class BrewRecipeEntry {
    /** Unique recipe ID for reference */
    public String id;

    // --- Fermentation Barrel fields ---
    /** Item ID for slot 0 (primary ingredient), e.g. "touhou_brews:koji_rice" */
    public String slot0;
    /** Item ID for slot 1 (secondary ingredient), null if not required */
    public String slot1;
    /** Slot 2 value: "water_bottle" or an item ID */
    public String slot2;
    /** Whether slot1 must be present */
    public boolean require_slot1 = false;

    // --- Infusion Jar fields ---
    /** Base spirit for infusion, e.g. "touhou_brews:ibuki_sake" */
    public String base;
    /** Fruit/additive for infusion */
    public String fruit;
    /** Sweetener for infusion */
    public String sweetener;
    /** Fixed output quality for infusion (typically 3) */
    public int output_quality = 3;

    // --- Shared fields ---
    /** Perfect processing time in ticks */
    public int perfect_time_ticks = 1200;
    /** BrewType ID of the output, e.g. "ibuki_sake", "beer", "custom_1" */
    public String output_brew_type;

    public BrewRecipeEntry() {}
}
