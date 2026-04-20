package com.wxffxx.touhoubrews.util;

import com.wxffxx.touhoubrews.config.BrewConfigManager;
import com.wxffxx.touhoubrews.config.QualityAlgorithmConfig;

import java.util.Map;
import java.util.TreeMap;

public class QualityAlgorithm {

    /**
     * Calculates the quality star rating (0-5) based on brewing parameters.
     * All thresholds and weights are read from QualityAlgorithmConfig.
     */
    public static int calculateQuality(int activeTimeTicks, int perfectTimeTicks, int ingredientPenalty) {
        QualityAlgorithmConfig cfg = BrewConfigManager.algorithm();

        // Ingredient score
        float ingredientScore = 1.0f - (ingredientPenalty * cfg.ingredient_penalty_per_extra);
        if (ingredientScore <= cfg.ingredient_spoil_threshold) return 0;

        // Timing score
        float timeRatio = (float) activeTimeTicks / perfectTimeTicks;
        float timingScore;

        if (timeRatio < cfg.time_undercook_ratio) {
            timingScore = cfg.time_undercook_score;
        } else if (timeRatio <= 1.0f) {
            timingScore = timeRatio;
        } else {
            timingScore = Math.max(0f, 1.0f - ((timeRatio - 1.0f) * cfg.time_overcook_penalty_rate));
        }

        float finalScore = timingScore * ingredientScore;

        // Sort thresholds descending and pick quality
        TreeMap<Float, Integer> sorted = new TreeMap<>(java.util.Collections.reverseOrder());
        for (Map.Entry<String, Float> entry : cfg.quality_thresholds.entrySet()) {
            try {
                sorted.put(entry.getValue(), Integer.parseInt(entry.getKey()));
            } catch (NumberFormatException ignored) {}
        }
        for (Map.Entry<Float, Integer> entry : sorted.entrySet()) {
            if (finalScore >= entry.getKey()) return entry.getValue();
        }
        return 0;
    }

    /**
     * Computes penalty for 1-to-1-to-1 recipes (unchanged).
     */
    public static int computePenalty(int... slotCounts) {
        int penalty = 0;
        for (int count : slotCounts) {
            if (count == 0) continue;
            penalty += Math.abs(count - 1);
        }
        return penalty;
    }
}
