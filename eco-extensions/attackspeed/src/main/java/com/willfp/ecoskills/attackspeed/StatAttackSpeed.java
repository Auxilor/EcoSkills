package com.willfp.ecoskills.attackspeed;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.yaml.YamlTransientConfig;
import com.willfp.ecoskills.api.EcoSkillsAPI;
import com.willfp.ecoskills.stats.Stat;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatAttackSpeed extends Stat {
    /**
     * Create attack speed stat.
     */
    public StatAttackSpeed() {
        super("attack_speed");
    }

    @NotNull
    @Override
    public Config loadConfig() {
        return new YamlTransientConfig(new YamlConfiguration());
    }

    @Override
    public void updateStatLevel(@NotNull final Player player) {
        AttributeModifier modifier = new AttributeModifier(
                this.getUuid(),
                this.getName(),
                EcoSkillsAPI.getInstance().getStatLevel(player, this) / 100D,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
        );

        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance == null) {
            return;
        }

        instance.removeModifier(modifier);

        this.getPlugin().getScheduler().run(() -> {
            instance.removeModifier(modifier);
            instance.addModifier(modifier);
        });
    }
}
