package com.willfp.ecoskills;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.ecoskills.commands.CommandEcoskills;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class EcoSkillsPlugin extends EcoPlugin {
    /**
     * Instance of EcoItems.
     */
    @Getter
    private static EcoSkillsPlugin instance;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoSkillsPlugin() {
        super(94630, 12205, "&#ff00ae");
        instance = this;
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
