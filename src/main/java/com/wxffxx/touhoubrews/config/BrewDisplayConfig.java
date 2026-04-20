package com.wxffxx.touhoubrews.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root config for brew display overrides.
 * Key = brew type ID (e.g. "ibuki_sake", "custom_1")
 */
public class BrewDisplayConfig {
    public Map<String, BrewDisplayEntry> brews = new LinkedHashMap<>();

    /** Returns display name for a quality level, or null if not configured. */
    public String getQualityName(String brewTypeId, int quality) {
        BrewDisplayEntry entry = brews.get(brewTypeId);
        if (entry == null) return null;
        return entry.quality_names.get(String.valueOf(quality));
    }

    /** Returns description override, or null if not configured. */
    public String getDescription(String brewTypeId) {
        BrewDisplayEntry entry = brews.get(brewTypeId);
        if (entry == null) return null;
        return entry.description;
    }
}
