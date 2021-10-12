package com.willfp.ecoskills.attackspeed;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.extensions.Extension;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedMain extends Extension {
    /**
     * The instance.
     */
    private static AttackSpeedMain instance;

    /**
     * attackspeed.yml.
     */
    private Config config = new AttackSpeedConfig(this);

    /**
     * Create a new extension for a plugin.
     *
     * @param plugin The plugin.
     */
    public AttackSpeedMain(@NotNull final EcoPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    protected void onEnable() {
        new StatAttackSpeed();
    }

    @Override
    protected void onDisable() {

    }

    /**
     * Get attackspeed.yml.
     *
     * @return The config.
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Get instance.
     *
     * @return The instance.
     */
    public static AttackSpeedMain getInstance() {
        return instance;
    }
}
