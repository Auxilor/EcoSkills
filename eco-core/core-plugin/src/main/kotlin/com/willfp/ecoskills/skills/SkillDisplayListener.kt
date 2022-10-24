package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.actionbar.ActionBarUtils
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getSkillProgress
import com.willfp.ecoskills.hasGainSoundEnabled
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
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

        plugin.scheduler.run {
            if (this.plugin.configYml.getBool("skills.progress.action-bar.enabled")) {
                var string = this.plugin.configYml.getFormattedString("skills.progress.action-bar.format")
                string = string.replace("%skill%", skill.name)
                string = string.replace("%current_xp%", NumberUtils.format(player.getSkillProgress(skill)))
                val nextLevel = skill.getExpForLevel(player.getSkillLevel(skill) + 1).toDouble()
                val nextLevelMessage =
                    if (nextLevel >= 2_000_000_000) plugin.langYml.getString("infinity") else NumberUtils.format(
                        nextLevel
                    )
                string = string.replace(
                    "%required_xp%",
                    nextLevelMessage
                )
                string = string.replace("%gained_xp%", NumberUtils.format(amount))
                ActionBarUtils.blacklist(player.uniqueId)
                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    *TextComponent.fromLegacyText(string)
                )
            }

            if (this.plugin.configYml.getBool("skills.progress.boss-bar.enabled")) {
                var string = this.plugin.configYml.getFormattedString("skills.progress.boss-bar.format")
                val currentXp = player.getSkillProgress(skill)
                string = string.replace("%skill%", skill.name)
                string = string.replace("%current_xp%", NumberUtils.format(currentXp))
                val nextLevel = skill.getExpForLevel(player.getSkillLevel(skill) + 1).toDouble()
                val nextLevelMessage =
                    if (nextLevel >= 2_000_000_000) plugin.langYml.getFormattedString("infinity") else NumberUtils.format(
                        nextLevel
                    )
                string = string.replace(
                    "%required_xp%",
                    nextLevelMessage
                )
                string = string.replace("%gained_xp%", NumberUtils.format(amount))
                val ratio = currentXp / nextLevel

                val key = plugin.namespacedKeyFactory.create("${player.name.substring(0, 3)}${skill.id}")

                val bossBar = Bukkit.getBossBar(key) ?: Bukkit.createBossBar(
                    key,
                    string,
                    BarColor.valueOf(this.plugin.configYml.getString("skills.progress.boss-bar.color").uppercase()),
                    BarStyle.valueOf(this.plugin.configYml.getString("skills.progress.boss-bar.style").uppercase())
                )

                if (ratio !in (0.0..1.0)) {
                    return@run
                }

                bossBar.setTitle(string)
                bossBar.progress = ratio
                bossBar.addPlayer(player)

                bossBarRemoves[key] =
                    System.currentTimeMillis() + this.plugin.configYml.getInt("skills.progress.boss-bar.duration")
            }

            if (this.plugin.configYml.getBool("skills.progress.sound.enabled") && player.hasGainSoundEnabled()) {
                val sound = Sound.valueOf(this.plugin.configYml.getString("skills.progress.sound.id").uppercase())
                val pitch = this.plugin.configYml.getDouble("skills.progress.sound.pitch")

                player.playSound(
                    player.location,
                    sound,
                    this.plugin.configYml.getDouble("skills.progress.sound.volume").toFloat(),
                    pitch.toFloat()
                )
            }
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
            val messages = mutableListOf<String>()
            val levelName =
                if (this.plugin.configYml.getBool("skills.level-up.message.level-as-numeral")) NumberUtils.toNumeral(
                    level
                ) else level.toString()

            for (string in this.plugin.configYml.getFormattedStrings("skills.level-up.message.message")) {
                messages.add(
                    string.replace("%skill%", skill.name)
                        .replace("%level%", levelName)
                )
            }

            val rewardIndex = messages.indexOf("%rewards%")
            if (rewardIndex != -1) {
                messages.removeAt(rewardIndex)
                messages.addAll(rewardIndex, skill.getRewardsMessages(player, level))
            }

            for (message in messages) {
                player.sendMessage(message)
            }
        }
    }

    companion object {
        private val bossBarRemoves = mutableMapOf<NamespacedKey, Long>()

        @JvmStatic
        fun tickBossBars(plugin: EcoPlugin) {
            plugin.scheduler.runTimer(5, 5) {
                for ((key, time) in bossBarRemoves.toMap()) {
                    if (time <= System.currentTimeMillis()) {
                        val bossBar = Bukkit.getBossBar(key) ?: continue
                        bossBar.removeAll()
                        Bukkit.removeBossBar(key)
                        bossBarRemoves.remove(key)
                    }
                }
            }
        }
    }
}