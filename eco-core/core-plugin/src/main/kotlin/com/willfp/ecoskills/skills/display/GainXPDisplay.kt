package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.actionbar.sendCompatibleActionBarMessage
import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.ecoskills.api.getFormattedRequiredXP
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getSkillProgress
import com.willfp.ecoskills.api.getSkillXP
import com.willfp.ecoskills.skills.Skill
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

private val xpGainSoundEnabledKey = PersistentDataKey(
    namespacedKeyOf("ecoskills", "gain_sound_enabled"),
    PersistentDataKeyType.BOOLEAN,
    true
)

fun Player.toggleXPGainSound() {
    this.profile.write(xpGainSoundEnabledKey, !this.profile.read(xpGainSoundEnabledKey))
}

val Player.isXPGainSoundEnabled: Boolean
    get() = this.profile.read(xpGainSoundEnabledKey)

class GainXPDisplay(
    private val plugin: EcoPlugin
) : Listener {
    private fun getHideBeforeLevel1(skill: Skill): Boolean {
        return (skill.getConfigFor("hide-before-level-1")).getBool("hide-before-level-1")
    }

    private fun getSound(skill: Skill): PlayableSound? {
        return if (skill.getConfigFor("gain-xp.sound.enabled").getBool("gain-xp.sound.enabled")) {
            PlayableSound.create(
                skill.getConfigFor("gain-xp.sound").getSubsection("gain-xp.sound")
            )
        } else null
    }

    @EventHandler
    fun handle(event: PlayerSkillXPGainEvent) {
        val player = event.player

        // Run next tick because level up calls before xp is added
        plugin.scheduler.run {
            handleActionBar(event)
            handleBossBar(event)

            if (player.isXPGainSoundEnabled) {
                getSound(event.skill)?.playTo(player)
            }
        }
    }

    private fun handleBossBar(event: PlayerSkillXPGainEvent) {
        if (!(event.skill.getConfigFor("gain-xp.boss-bar.enabled")).getBool("gain-xp.boss-bar.enabled")) {
            return
        }

        val player = event.player
        val skill = event.skill

        val message = (event.skill.getConfigFor("gain-xp.boss-bar.format"))
            .getString("gain-xp.boss-bar.format")
            .formatMessage(event)

        player.sendTemporaryBossBar(
            message,
            event.skill.id,
            (event.skill.getConfigFor("gain-xp.boss-bar.duration")).getInt("gain-xp.boss-bar.duration"),
            BarColor.valueOf((event.skill.getConfigFor("gain-xp.boss-bar.color"))
                .getString("gain-xp.boss-bar.color").uppercase()),
            BarStyle.valueOf((event.skill.getConfigFor("gain-xp.boss-bar.style"))
                .getString("gain-xp.boss-bar.style").uppercase()),
            player.getSkillProgress(skill).coerceIn(0.0..1.0)
        )
    }

    private fun handleActionBar(event: PlayerSkillXPGainEvent) {
        if (!(event.skill.getConfigFor("gain-xp.action-bar.enabled"))
                .getBool("gain-xp.action-bar.enabled")) {
            return
        }

        val player = event.player

        val message = (event.skill.getConfigFor("gain-xp.action-bar.message"))
            .getString("gain-xp.action-bar.message")
            .formatMessage(event)

        player.sendCompatibleActionBarMessage(message)
    }

    private fun String.formatMessage(event: PlayerSkillXPGainEvent): String =
        this.replace(
            "%skill%",
            if (event.player.getSkillLevel(event.skill) > 0 || !getHideBeforeLevel1(event.skill)) event.skill.name else plugin.langYml.getString(
                "learning-skill"
            )
        )
            .replace("%current_xp%", event.player.getSkillXP(event.skill).toNiceString())
            .replace("%required_xp%", event.player.getFormattedRequiredXP(event.skill))
            .replace("%gained_xp%", event.gainedXP.toNiceString())
            .formatEco(event.player, true)
}
