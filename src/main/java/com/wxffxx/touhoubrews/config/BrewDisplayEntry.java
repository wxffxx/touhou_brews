package com.wxffxx.touhoubrews.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Display configuration for a single brew type.
 * Config overrides for names and descriptions per quality level.
 */
public class BrewDisplayEntry {
    /** Description text shown in tooltip (overrides lang key if present) */
    public String description;

    /** Quality level → display name map. Key is quality as string "0"-"6". */
    public Map<String, String> quality_names = new LinkedHashMap<>();

    public BrewDisplayEntry() {}

    public BrewDisplayEntry(String description, Map<String, String> qualityNames) {
        this.description = description;
        this.quality_names = qualityNames;
    }
}
