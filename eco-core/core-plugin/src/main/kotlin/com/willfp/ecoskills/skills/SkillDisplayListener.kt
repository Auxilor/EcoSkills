package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getSkillProgress
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class SkillDisplayListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onProgress(event: PlayerSkillExpGainEvent) {
        val player = event.player
        val skill = event.skill
        val amount = event.amount

        if (this.plugin.configYml.getBool("skills.progress.action-bar.enabled")) {
            var string = this.plugin.configYml.getString("skills.progress.action-bar.format")
            string = string.replace("%skill%", skill.name)
            string = string.replace("%current_xp%", NumberUtils.format(player.getSkillProgress(skill)))
            string = string.replace(
                "%required_xp%",
                NumberUtils.format(
                    skill.getExpForLevel(player.getSkillLevel(skill) + 1).toDouble()
                )
            )
            string = string.replace("%gained_xp%", NumberUtils.format(amount))
            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                *TextComponent.fromLegacyText(string)
            )
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onLevelUp(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val level = event.level

        if (this.plugin.configYml.getBool("skills.level-up.sound.enabled")) {
            val sound = Sound.valueOf(this.plugin.configYml.getString("skills.level-up.sound.id").uppercase())
            val pitch = this.plugin.configYml.getDouble("skills.level-up.sound.pitch")

            player.playSound(
                player.location,
                sound,
                100f,
                pitch.toFloat()
            )
        }

        if (this.plugin.configYml.getBool("skills.level-up.message.enabled")) {
            val messages = ArrayList<String>()

            for (string in this.plugin.configYml.getStrings("skills.level-up.message.message")) {
                messages.add(
                    string.replace("%skill%", skill.name)
                        .replace("%level%", level.toString())
                )
            }

            val rewardIndex = messages.indexOf("%rewards%")
            if (rewardIndex != -1) {
                messages.removeAt(rewardIndex)
                messages.addAll(rewardIndex, listOf())
            }

            for (message in messages) {
                player.sendMessage(message)
            }
        }
    }
}