package com.willfp.ecoskills.actionbar

import com.willfp.ecoskills.plugin
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import wiki.ecohub.core.platform.namespacedKeyOf
import wiki.ecohub.data.PersistentDataKey
import wiki.ecohub.data.PersistentDataKeyType
import wiki.ecohub.game.entity.player.asAudience
import wiki.ecohub.game.entity.player.profile
import wiki.ecohub.ui.display.actionbar.PersistentActionBar
import wiki.ecohub.ui.display.actionbar.PersistentActionBars
import wiki.ecohub.ui.placeholder.InjectablePlaceholder
import wiki.ecohub.ui.placeholder.PlaceholderInjectable
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.placeholder.type.PlayerStaticPlaceholder
import wiki.ecohub.ui.text.containsIgnoreCase
import wiki.ecohub.ui.text.toComponent

private val actionBarEnabledKey = PersistentDataKey(
    namespacedKeyOf("ecoskills", "actionbar_enabled"),
    PersistentDataKeyType.BOOLEAN,
    true
)

fun Player.togglePersistentActionBar() {
    this.profile.write(actionBarEnabledKey, !this.profile.read(actionBarEnabledKey))
}

val Player.isPersistentActionBarEnabled: Boolean
    get() = this.profile.read(actionBarEnabledKey)

fun Player.sendCompatibleActionBarMessage(message: String) {
    this.asAudience().sendActionBar(message.toComponent())
}

object ActionBarHandler {
    private val disabledWorlds = plugin.configYml
        .getStrings("persistent-action-bar.disabled-in-worlds")

    private val hiddenGameModes = setOf(GameMode.CREATIVE, GameMode.SPECTATOR)

    private var persistentActionBar: PersistentActionBar? = null

    internal fun reload() {
        if (!plugin.configYml.getBool("persistent-action-bar.enabled")) {
            persistentActionBar?.unregister()
            persistentActionBar = null
            return
        }

        persistentActionBar = PersistentActionBars.register(
            plugin,
            "persistent",
            plugin.configYml.getIntOrNull("persistent-action-bar.priority") ?: 50
        ) { render(it) }
    }

    private fun render(player: Player): Component? {
        if (!player.isPersistentActionBarEnabled) {
            return null
        }

        if (plugin.configYml.getBool("persistent-action-bar.require-permission")) {
            if (!player.hasPermission("ecoskills.enable-persistent-action-bar")) {
                return null
            }
        }

        if (disabledWorlds.containsIgnoreCase(player.world.name)) {
            return null
        }

        if (player.gameMode in hiddenGameModes) {
            return null
        }

        if (plugin.configYml.getBool("persistent-action-bar.scale-health")) {
            if (!player.isHealthScaled || player.healthScale != 20.0) {
                player.isHealthScaled = true
                player.healthScale = 20.0
            }
        }

        return plugin.configYml
            .getFormattedString(
                "persistent-action-bar.format", placeholderContext(
                    player = player,
                    injectable = PlayerHealthInjectable
                )
            )
            .toComponent()
    }

    object PlayerHealthInjectable : PlaceholderInjectable {
        private val injections = listOf(
            PlayerStaticPlaceholder(
                "health"
            ) { it.health.toInt().toString() },
            PlayerStaticPlaceholder(
                "max_health"
            ) { it.getAttribute(Attribute.MAX_HEALTH)?.value?.toInt()?.toString() ?: "20" },
        )

        override fun getPlaceholderInjections(): List<InjectablePlaceholder> {
            return injections
        }

        override fun addInjectablePlaceholder(p0: Iterable<InjectablePlaceholder>) {
            return
        }

        override fun clearInjectedPlaceholders() {
            return
        }
    }
}

object HealthScaleDisabler : Listener {
    @EventHandler
    fun handle(event: PlayerJoinEvent) {
        if (!plugin.configYml.getBool("persistent-action-bar.scale-health")) {
            event.player.isHealthScaled = false
        }
    }
}
