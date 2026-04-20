package com.wxffxx.touhoubrews.config;

/**
 * A single potion effect entry in the brew config.
 * Specifies exactly what happens at a given quality level — no formulas.
 */
public class BrewEffectEntry {
    public String effect;    // e.g. "minecraft:strength"
    public int duration;     // in ticks
    public int amplifier;    // 0 = level I, 1 = level II, etc.

    public BrewEffectEntry() {}

    public BrewEffectEntry(String effect, int duration, int amplifier) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }
}
