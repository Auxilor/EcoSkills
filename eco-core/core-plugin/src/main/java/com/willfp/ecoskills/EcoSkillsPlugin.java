package com.willfp.ecoskills;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.ecoskills.commands.CommandEcoskills;
import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.effects.Effects;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class EcoSkillsPlugin extends EcoPlugin {
    /**
     * Instance of EcoItems.
     */
    private static EcoSkillsPlugin instance;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoSkillsPlugin() {
        super(94630, 12205, "&#ff00ae");
        instance = this;
    }

    @Override
    protected void handleReload() {
        for (Effect effect : Effects.values()) {
            this.getEventManager().unregisterListener(effect);
            this.getEventManager().registerListener(effect);
        }
    }

    /**
     * Get the instance of EcoSkills.
     *
     * @return Instance.
     */
    public static EcoSkillsPlugin getInstance() {
        return instance;
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(

        );
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new CommandEcoskills(this)
        );
    }
}
