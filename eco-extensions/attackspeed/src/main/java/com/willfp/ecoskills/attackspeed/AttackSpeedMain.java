package com.willfp.ecoskills.attackspeed;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.extensions.Extension;
import com.willfp.ecoskills.stats.Stat;
import org.jetbrains.annotations.NotNull;

public class AttackSpeedMain extends Extension {
    /**
     * Attack Speed.
     */
    public static final Stat ATTACK_SPEED = new StatAttackSpeed();

    /**
     * Create a new extension for a plugin.
     *
     * @param plugin The plugin.
     */
    public AttackSpeedMain(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
