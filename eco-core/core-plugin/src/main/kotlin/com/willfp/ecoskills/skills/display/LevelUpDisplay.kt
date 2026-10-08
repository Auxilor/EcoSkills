package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.toComponent
import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.runOwned
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object LevelUpDisplay : Listener {
    private val sound = PlayableSound.create(plugin.configYml.getSubsection("skills.level-up.sound"))

    private val soundsToPlay = ConcurrentHashMap.newKeySet<UUID>()

    internal fun startTickingSounds() {
        plugin.scheduler.global().runTimer(1, 1) {
            val iterator = soundsToPlay.iterator()

            while (iterator.hasNext()) {
                val player = Bukkit.getPlayer(iterator.next())
                iterator.remove()
                player?.runOwned { sound?.playTo(player) }
            }
        }
    }

    @EventHandler
    fun handle(event: PlayerSkillLevelUpEvent) {
        val player = event.player
        val skill = event.skill
        val level = event.level

        soundsToPlay += player.uniqueId

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
    }
}
