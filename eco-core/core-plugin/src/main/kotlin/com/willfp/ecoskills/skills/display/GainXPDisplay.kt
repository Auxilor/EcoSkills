package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.actionbar.sendCompatibleActionBarMessage
import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.ecoskills.api.getFormattedRequiredXP
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getSkillProgress
import com.willfp.ecoskills.api.getSkillXP
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GainXPDisplay(
    private val plugin: EcoPlugin
) : Listener {
    private val sound = if (plugin.configYml.getBool("skills.gain-xp.sound.enabled")) {
        PlayableSound.create(
            plugin.configYml.getSubsection("skills.gain-xp.sound")
        )
    } else null

    @EventHandler
    fun handle(event: PlayerSkillXPGainEvent) {
        val player = event.player

        // Run next tick because level up calls before xp is added
        plugin.scheduler.run {
            handleActionBar(event)
            handleBossBar(event)

            sound?.playTo(player)
        }
    }

    private fun handleBossBar(event: PlayerSkillXPGainEvent) {
        if (!plugin.configYml.getBool("skills.gain-xp.boss-bar.enabled")) {
            return
        }

        val player = event.player
        val skill = event.skill

        val message = plugin.configYml.getString("skills.gain-xp.boss-bar.message")
            .formatMessage(event)

        player.sendTemporaryBossBar(
            message,
            event.skill.id,
            this.plugin.configYml.getInt("skills.progress.boss-bar.duration"),
            BarColor.valueOf(this.plugin.configYml.getString("skills.progress.boss-bar.color").uppercase()),
            BarStyle.valueOf(this.plugin.configYml.getString("skills.progress.boss-bar.style").uppercase()),
            player.getSkillProgress(skill).coerceIn(0.0..1.0)
        )
    }

    private fun handleActionBar(event: PlayerSkillXPGainEvent) {
        if (!plugin.configYml.getBool("skills.gain-xp.action-bar.enabled")) {
            return
        }

        val player = event.player

        val message = plugin.configYml.getString("skills.gain-xp.action-bar.message")
            .formatMessage(event)

        player.sendCompatibleActionBarMessage(message)
    }

    private fun String.formatMessage(event: PlayerSkillXPGainEvent): String =
        this.replace(
            "%skill%",
            if (event.player.getSkillLevel(event.skill) > 0) event.skill.name else plugin.langYml.getString("learning-skill")
        )
            .replace("%current_xp%", event.player.getSkillXP(event.skill).toNiceString())
            .replace("%required_xp%", event.player.getFormattedRequiredXP(event.skill))
            .replace("%gained_xp%", event.gainedXP.toNiceString())
            .formatEco(event.player)
}
