package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.toComponent
import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.skills.Skill
import net.kyori.adventure.title.Title
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class LevelUpDisplay(
    private val plugin: EcoPlugin
) : Listener {
    private fun getSound(skill: Skill): PlayableSound? {
        return if (skill.getConfigFor("level-up.sound.enabled").getBool("level-up.sound.enabled")) {
            PlayableSound.create(
                skill.getConfigFor("level-up.sound").getSubsection("level-up.sound")
            )
        } else null
    }

    @EventHandler
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val level = event.level

        if ((skill.getConfigFor("gain-xp.boss-bar.color")).getBool("gain-xp.boss-bar.color")) {
            val rawMessage = skill.getConfigFor("level-up.message.message")
                .getStrings("level-up.message.message")

            val formatted = skill.addPlaceholdersInto(
                rawMessage,
                player,
                level = level
            )

            formatted.forEach { player.sendMessage(it) }
        }

        if ((skill.getConfigFor("level-up.title.enabled")).getBool("level-up.title.enabled")) {
            val rawTitle = skill.getConfigFor("level-up.title.title").getString("level-up.title.title")
            val rawSubtitle = skill.getConfigFor("level-up.title.subtitle")
                .getString("level-up.title.subtitle")

            val formatted = skill.addPlaceholdersInto(
                listOf(rawTitle, rawSubtitle),
                player,
                level = level
            )

            player.showTitle(Title.title(
                formatted[0].toComponent(),
                formatted[1].toComponent(),
                Title.Times.times(
                    Duration.ofSeconds((skill.getConfigFor("level-up.title.fade-in")
                        .getInt("level-up.title.fade-in")/20).toLong()),
                    Duration.ofSeconds((skill.getConfigFor("level-up.title.fade-in")
                        .getInt("level-up.title.stay")/20).toLong()),
                    Duration.ofSeconds((skill.getConfigFor("level-up.title.fade-in")
                        .getInt("level-up.title.fade-out")/20).toLong())
                )
            ))
        }

        getSound(event.skill)?.playTo(player)
    }
}
