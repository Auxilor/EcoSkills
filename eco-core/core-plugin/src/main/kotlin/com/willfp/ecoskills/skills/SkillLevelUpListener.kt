package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SkillLevelUpListener(
    private val plugin: EcoPlugin
) : Listener {
    private val sound = if (plugin.configYml.getBool("level-up.sound.enabled")) {
        PlayableSound.create(
            plugin.configYml.getSubsection("level-up.sound")
        )
    } else null

    @EventHandler
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val level = event.level

        if (plugin.configYml.getBool("level-up.message.enabled")) {
            val rawMessage = plugin.configYml.getStrings("level-up.message.message")

            val formatted = skill.addPlaceholdersInto(
                rawMessage,
                player,
                level = level
            )

            formatted.forEach { player.sendMessage(it) }
        }

        sound?.playTo(player)
    }
}
