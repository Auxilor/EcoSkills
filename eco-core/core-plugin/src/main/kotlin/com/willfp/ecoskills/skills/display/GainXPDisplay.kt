package com.willfp.ecoskills.skills.display

import com.willfp.ecoskills.actionbar.ActionBarHandler
import com.willfp.ecoskills.actionbar.sendCompatibleActionBarMessage
import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.ecoskills.api.getFormattedRequiredXP
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.api.getSkillProgress
import com.willfp.ecoskills.api.getSkillXP
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import wiki.ecohub.core.platform.namespacedKeyOf
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.data.PersistentDataKey
import wiki.ecohub.data.PersistentDataKeyType
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.entity.player.profile
import wiki.ecohub.game.entity.runOwned
import wiki.ecohub.game.sound.PlayableSound
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.text.formatEco
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val xpGainSoundEnabledKey = PersistentDataKey(
    namespacedKeyOf("ecoskills", "gain_sound_enabled"),
    PersistentDataKeyType.BOOLEAN,
    true
)

fun Player.toggleXPGainSound() {
    this.profile.write(xpGainSoundEnabledKey, !this.profile.read(xpGainSoundEnabledKey))
}

data class PlayerSkill(
    val player: UUID,
    val skill: String
)

private fun playerSkill(player: Player, skill: Skill): PlayerSkill {
    return PlayerSkill(player.uniqueId, skill.id)
}

val Player.isXPGainSoundEnabled: Boolean
    get() = this.profile.read(xpGainSoundEnabledKey)

object GainXPDisplay : Listener {
    private val gainCache: EcoCache<PlayerSkill, Double> = EcoCache.builder<PlayerSkill, Double>().expireAfterWrite(Duration.ofSeconds(3))
        .build()

    private val sound = PlayableSound.create(plugin.configYml.getSubsection("skills.gain-xp.sound"))

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

    @EventHandler(priority = EventPriority.MONITOR)
    fun handle(event: PlayerSkillXPGainEvent) {
        val player = event.player
        val current = gainCache.get(playerSkill(player, event.skill)) { 0.0 }
        gainCache.put(playerSkill(player, event.skill), current + event.gainedXP)

        if (player.isXPGainSoundEnabled) {
            soundsToPlay += event.player.uniqueId
        }

        // Run next tick because level up calls before xp is added
        plugin.scheduler.on(player).run {
            handleActionBar(event)
            handleBossBar(event)
        }
    }

    private fun handleBossBar(event: PlayerSkillXPGainEvent) {
        if (!plugin.configYml.getBool("skills.gain-xp.boss-bar.enabled")) {
            return
        }

        val player = event.player
        val skill = event.skill

        val message = plugin.configYml.getString("skills.gain-xp.boss-bar.format")
            .formatMessage(event)

        player.sendTemporaryBossBar(
            message,
            event.skill.id,
            plugin.configYml.getInt("skills.gain-xp.boss-bar.duration"),
            BarColor.valueOf(plugin.configYml.getString("skills.gain-xp.boss-bar.color").uppercase()),
            BarStyle.valueOf(plugin.configYml.getString("skills.gain-xp.boss-bar.style").uppercase()),
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
            if (event.player.getSkillLevel(event.skill) > 0 || !event.skill.isHiddenBeforeLevel1) event.skill.name else plugin.langYml.getString(
                "learning-skill"
            )
        )
            .replace("%percentage_progress%", (event.player.getSkillProgress(event.skill) * 100).toNiceString())
            .replace("%current_xp%", event.player.getSkillXP(event.skill).toNiceString())
            .replace("%required_xp%", event.player.getFormattedRequiredXP(event.skill))
            .replace("%gained_xp%", gainCache.get(playerSkill(event.player, event.skill)) { 0.0 }.toNiceString())
            .formatEco(
                placeholderContext(
                    event.player,
                    injectable = ActionBarHandler.PlayerHealthInjectable
                )
            )
}
