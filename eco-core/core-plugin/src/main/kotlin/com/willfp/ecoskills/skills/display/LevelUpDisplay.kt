package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.toComponent
import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class LevelUpDisplay(
    private val plugin: EcoPlugin
) : Listener {
    private val sound = if (plugin.configYml.getBool("skills.level-up.sound.enabled")) {
        PlayableSound.create(
            plugin.configYml.getSubsection("skills.level-up.sound")
        )
    } else null

    @EventHandler
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val level = event.level

        if (plugin.configYml.getBool("skills.level-up.message.enabled")) {
            val rawMessage = plugin.configYml.getStrings("skills.level-up.message.message")

            val formatted = skill.addPlaceholdersInto(
                rawMessage,
                player,
                level = level
            )

            formatted.forEach { player.sendMessage(it) }
        }

        if (plugin.configYml.getBool("skills.level-up.title.enabled")) {
            val rawTitle = plugin.configYml.getString("skills.level-up.title.title")
            val rawSubtitle = plugin.configYml.getString("skills.level-up.title.subtitle")

            val formatted = skill.addPlaceholdersInto(
                listOf(rawTitle, rawSubtitle),
                player,
                level = level
            )

            player.showTitle(
                Title.title(
                    formatted[0].toComponent(),
                    formatted[1].toComponent(),
                    Title.Times.times(
                        Duration.ofMillis((plugin.configYml.getDouble("skills.level-up.title.fade-in") * 1000).toLong()),
                        Duration.ofMillis((plugin.configYml.getDouble("skills.level-up.title.stay") * 1000).toLong()),
                        Duration.ofMillis((plugin.configYml.getDouble("skills.level-up.title.fade-out") * 1000).toLong())
                    )
                )
            )
        }

        sound?.playTo(player)
    }
}
