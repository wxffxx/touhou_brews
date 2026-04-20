package com.wxffxx.touhoubrews.config;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Root config container for brew effects.
 * Deserialized from config/touhou_brews/brew_effects.json
 */
public class BrewEffectsConfig {
    /** Map of brew type ID → (quality level string → list of effects) */
    public Map<String, Map<String, List<BrewEffectEntry>>> brews = new HashMap<>();

    /**
     * Returns effects for a given brew type and quality (0-6).
     * Returns empty list if not configured.
     */
    public List<BrewEffectEntry> getEffects(String brewTypeId, int quality) {
        Map<String, List<BrewEffectEntry>> byQuality = brews.get(brewTypeId);
        if (byQuality == null) return List.of();
        List<BrewEffectEntry> entries = byQuality.get("quality_" + quality);
        return entries != null ? entries : List.of();
    }
}
