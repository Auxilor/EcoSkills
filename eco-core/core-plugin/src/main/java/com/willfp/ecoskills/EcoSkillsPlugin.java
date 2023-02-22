package com.willfp.ecoskills;

import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.Prerequisite;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecoskills.actionbar.ActionBarClearOnGamemode;
import com.willfp.ecoskills.actionbar.ActionBarCompatChatMessage;
import com.willfp.ecoskills.actionbar.ActionBarCompatSetActionBar;
import com.willfp.ecoskills.actionbar.ActionBarUtils;
import com.willfp.ecoskills.actionbar.HealthScaleListener;
import com.willfp.ecoskills.commands.CommandEcoskills;
import com.willfp.ecoskills.commands.CommandFlySpeed;
import com.willfp.ecoskills.commands.CommandSkills;
import com.willfp.ecoskills.commands.CommandWalkSpeed;
import com.willfp.ecoskills.config.EffectsYml;
import com.willfp.ecoskills.data.DataListener;
import com.willfp.ecoskills.data.LeaderboardHandler;
import com.willfp.ecoskills.effects.CustomEffectUtils;
import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.effects.Effects;
import com.willfp.ecoskills.integrations.EcoEnchantsEnchantingLeveller;
import com.willfp.ecoskills.integrations.enchantgui.EnchantGuiHandler;
import com.willfp.ecoskills.placeholders.EcoSkillsTopPlaceholder;
import com.willfp.ecoskills.skills.CustomSkillTriggerXPGainListener;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.skills.SkillDisplayListener;
import com.willfp.ecoskills.skills.SkillLevellingListener;
import com.willfp.ecoskills.skills.Skills;
import com.willfp.ecoskills.stats.CustomStatUtils;
import com.willfp.ecoskills.stats.DamageIndicatorListener;
import com.willfp.ecoskills.stats.Stat;
import com.willfp.ecoskills.stats.Stats;
import com.willfp.ecoskills.stats.modifier.StatModifierListener;
import com.willfp.libreforge.LibReforgePlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EcoSkillsPlugin extends LibReforgePlugin {
    /**
     * Instance of EcoSkills.
     */
    private static EcoSkillsPlugin instance;

    /**
     * effects.yml.
     */
    private final EffectsYml effectsYml;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoSkillsPlugin() {
        instance = this;
        effectsYml = new EffectsYml(this);

        this.registerJavaHolderProvider(CustomEffectUtils::getCustomEffects);
        this.registerJavaHolderProvider(CustomStatUtils::getCustomStats);
    }

    @Override
    public void handleEnableAdditional() {
        EcoSkillsTopPlaceholder.register(this);

        this.copyConfigs("customstats");
        this.copyConfigs("customeffects");
        this.copyConfigs("customskills");

        Skills.update(this);
    }

    @Override
    public void handleReloadAdditional() {
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

        if (this.getConfigYml().getBool("persistent-action-bar.enabled")) {
            ActionBarUtils.startRunnable();
        }

        SkillDisplayListener.tickBossBars(this);
        this.getScheduler().runAsyncTimer(new LeaderboardHandler.Runnable(), 50, 2400);
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
                new DamageIndicatorListener(this),
                new HealthScaleListener(this),
                new ActionBarClearOnGamemode(),
                new CustomSkillTriggerXPGainListener()
        );
    }

    @Override
    protected List<AbstractPacketAdapter> loadPacketAdapters() {
        if (Prerequisite.HAS_1_19.isMet()) {
            return List.of(
                    new ActionBarCompatSetActionBar(this)
            );
        }

        return Arrays.asList(
                new ActionBarCompatChatMessage(this),
                new ActionBarCompatSetActionBar(this)
        );
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new CommandEcoskills(this),
                new CommandSkills(this),
                new CommandWalkSpeed(this),
                new CommandFlySpeed(this)
        );
    }

    @NotNull
    @Override
    public List<IntegrationLoader> loadAdditionalIntegrations() {
        return List.of(
                new IntegrationLoader("EcoEnchants", () -> this.getEventManager().registerListener(new EcoEnchantsEnchantingLeveller(this))),
                new IntegrationLoader("EnchantGui", () -> this.getEventManager().registerListener(new EnchantGuiHandler()))
        );
    }

    @NotNull
    @Override
    public String getMinimumEcoVersion() {
        return "6.50.0";
    }
}
