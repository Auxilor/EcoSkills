package com.willfp.ecoskills.actionbar

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.containsIgnoreCase
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import java.util.UUID

private const val ACTION_BAR_DURATION = 3000L
private const val TICK_DURATION = 50L

private val blacklist = mutableMapOf<UUID, Long>()

private val whitelist = mutableMapOf<UUID, Long>()

fun Player.pausePersistentActionBar() {
    if (isSendingPersistentActionBar) {
        return
    }

    blacklist[this.uniqueId] = System.currentTimeMillis() + ACTION_BAR_DURATION
}

private val Player.isPersistentActionBarPaused: Boolean
    get() {
        val time = blacklist[this.uniqueId] ?: return false
        return time > System.currentTimeMillis()
    }

private fun Player.sendPersistentActionBar(string: String) {
    whitelist[this.uniqueId] = System.currentTimeMillis() + TICK_DURATION

    // Have to use the shit method for compatibility.
    @Suppress("DEPRECATION")
    this.spigot().sendMessage(
        net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
        *net.md_5.bungee.api.chat.TextComponent.fromLegacyText(string)
    )
}

private val Player.isSendingPersistentActionBar: Boolean
    get() {
        val time = whitelist[this.uniqueId] ?: return false
        return time > System.currentTimeMillis()
    }

object ActionBarGamemodeListener : Listener {
    @EventHandler
    fun handle(event: PlayerGameModeChangeEvent) {
        if (event.newGameMode in setOf(GameMode.CREATIVE, GameMode.SPECTATOR)) {
            @Suppress("DEPRECATION")
            event.player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                *net.md_5.bungee.api.chat.TextComponent.fromLegacyText("")
            )
        }
    }
}

class ActionBarHandler(
    private val plugin: EcoPlugin
) {
    private val disabledWorlds = plugin.configYml
        .getStrings("persistent-action-bar.disabled-in-worlds")

    private fun trySendMessage(player: Player) {
        if (player.isPersistentActionBarPaused) {
            return
        }

        if (disabledWorlds.containsIgnoreCase(player.world.name)) {
            return
        }

        if (player.gameMode in setOf(GameMode.CREATIVE, GameMode.SPECTATOR)) {
            return
        }

        if (plugin.configYml.getBool("persistent-action-bar.scale-health")) {
            player.isHealthScaled = true
            player.healthScale = 20.0
        }

        player.sendPersistentActionBar(
            plugin.configYml
                .getFormattedString(
                    "persistent-action-bar.format", placeholderContext(
                        player = player
                    )
                )
        )
    }

    internal fun beginTickingActionBar() {
        plugin.scheduler.runTimer(5, 5) {
            for (player in Bukkit.getOnlinePlayers()) {
                trySendMessage(player)
            }
        }
    }
}
