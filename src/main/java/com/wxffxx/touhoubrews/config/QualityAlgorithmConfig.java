package com.wxffxx.touhoubrews.config;

import java.util.Map;
import java.util.TreeMap;

/**
 * Config for the quality scoring algorithm.
 * Deserialized from config/touhou_brews/quality_algorithm.json
 */
public class QualityAlgorithmConfig {

    /** How much score to deduct per extra/missing ingredient (default 0.1) */
    public float ingredient_penalty_per_extra = 0.1f;

    /** If ingredient score falls at or below this, return quality 0 immediately (default 0.2) */
    public float ingredient_spoil_threshold = 0.2f;

    /** If time ratio < this value, use time_undercook_score instead of linear (default 0.5) */
    public float time_undercook_ratio = 0.5f;

    /** Score assigned when brew is pulled too early (default 0.2) */
    public float time_undercook_score = 0.2f;

    /**
     * When time ratio > 1.0, score is reduced by this rate per unit of over-time.
     * e.g. 1.5 means at 2x time, score drops by 1.5 → 0. (default 1.5)
     */
    public float time_overcook_penalty_rate = 1.5f;

    /**
     * finalScore thresholds for each star rating.
     * Key = quality level (as string), value = minimum score needed.
     * Must be sorted descending (handled in algorithm).
     */
    public Map<String, Float> quality_thresholds = new TreeMap<>(Map.of(
            "5", 0.95f,
            "4", 0.80f,
            "3", 0.60f,
            "2", 0.40f,
            "1", 0.20f
    ));
}
