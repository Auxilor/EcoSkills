package com.willfp.ecoskills;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecoskills.commands.CommandEcoskills;
import com.willfp.ecoskills.commands.CommandSkills;
import com.willfp.ecoskills.config.EffectsYml;
import com.willfp.ecoskills.data.DataListener;
import com.willfp.ecoskills.data.LeaderboardHandler;
import com.willfp.ecoskills.data.PlayerBlockListener;
import com.willfp.ecoskills.data.SaveHandler;
import com.willfp.ecoskills.data.storage.DataHandler;
import com.willfp.ecoskills.data.storage.MySQLDataHandler;
import com.willfp.ecoskills.data.storage.YamlDataHandler;
import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.effects.Effects;
import com.willfp.ecoskills.integrations.EcoEnchantsEnchantingLeveller;
import com.willfp.ecoskills.integrations.afk.AFKHandlerKt;
import com.willfp.ecoskills.integrations.afk.impl.AFKIntegrationEssentials;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.skills.SkillDisplayListener;
import com.willfp.ecoskills.skills.SkillLevellingListener;
import com.willfp.ecoskills.skills.Skills;
import com.willfp.ecoskills.stats.DamageIndicatorListener;
import com.willfp.ecoskills.stats.Stat;
import com.willfp.ecoskills.stats.Stats;
import com.willfp.ecoskills.stats.modifier.StatModifierListener;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class EcoSkillsPlugin extends EcoPlugin {
    /**
     * Instance of EcoSkills.
     */
    private static EcoSkillsPlugin instance;

    /**
     * data.yml.
     */
    private final DataHandler dataHandler;

    /**
     * effects.yml.
     */
    private final EffectsYml effectsYml;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoSkillsPlugin() {
        super(1351, 12205, "&#ff00ae", true);
        instance = this;
        effectsYml = new EffectsYml(this);
        dataHandler = this.getConfigYml().getBool("mysql.enabled")
                ? new MySQLDataHandler(this) : new YamlDataHandler(this);
    }

    @Override
    protected void handleReload() {
        for (Effect effect : Effects.values()) {
            this.getEventManager().unregisterListener(effect);
            this.getEventManager().registerListener(effect);
        }
        for (Stat stat : Stats.values()) {
            this.getEventManager().unregisterListener(stat);
            this.getEventManager().registerListener(stat);
        }
        for (Skill skill : Skills.values()) {
            this.getEventManager().unregisterListener(skill);
            this.getEventManager().registerListener(skill);
        }
        SaveHandler.Companion.save(this);

        this.getScheduler().runTimer(new SaveHandler.Runnable(this), 20000, 20000);
        this.getScheduler().runTimer(new LeaderboardHandler.Runnable(), 50, 2400);
    }

    @Override
    protected void handleDisable() {
        SaveHandler.Companion.save(this);
    }

    /**
     * Get data handler.
     *
     * @return data handler.
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Get effects.yml.
     *
     * @return effects.yml.
     */
    public EffectsYml getEffectsYml() {
        return effectsYml;
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
                new SkillLevellingListener(),
                new SkillDisplayListener(this),
                new StatModifierListener(),
                new DataListener(),
                new PlayerBlockListener(this),
                new EcoSkillsEventModifierHandler(this)
        );
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new CommandEcoskills(this),
                new CommandSkills(this)
        );
    }

    @Override
    protected List<IntegrationLoader> loadIntegrationLoaders() {
        return Arrays.asList(
                new IntegrationLoader("HolographicDisplays", () -> this.getEventManager().registerListener(new DamageIndicatorListener(this))),
                new IntegrationLoader("EcoEnchants", () -> this.getEventManager().registerListener(new EcoEnchantsEnchantingLeveller(this))),
                new IntegrationLoader("Essentials", () -> AFKHandlerKt.registerIntegration(new AFKIntegrationEssentials()))
        );
    }

    @Override
    public String getMinimumEcoVersion() {
        return "6.6.0";
    }
}
