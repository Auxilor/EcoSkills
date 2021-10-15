package com.willfp.ecoskills.attackspeed;

import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.yaml.YamlBaseConfig;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedConfig extends YamlBaseConfig {
    public AttackSpeedConfig(@NotNull final PluginLike plugin) {
        super("attackspeed", true, plugin);
    }
}
