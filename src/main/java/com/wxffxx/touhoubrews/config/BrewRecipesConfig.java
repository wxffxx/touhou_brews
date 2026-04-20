package com.wxffxx.touhoubrews.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Root config for brew recipes (both fermentation barrel and infusion jar).
 */
public class BrewRecipesConfig {
    public List<BrewRecipeEntry> fermentation_barrel = new ArrayList<>();
    public List<BrewRecipeEntry> infusion_jar = new ArrayList<>();
}
