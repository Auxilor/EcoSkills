package com.willfp.ecoskills.attackspeed;

import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.config.ConfigType;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedConfig extends BaseConfig {
    public AttackSpeedConfig(@NotNull final PluginLike plugin) {
        super("attackspeed", plugin, true, ConfigType.YAML);
    }
}
