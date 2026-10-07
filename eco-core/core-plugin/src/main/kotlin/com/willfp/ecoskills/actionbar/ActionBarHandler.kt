package com.willfp.ecoskills.actionbar

import com.willfp.eco.core.actionbar.PersistentActionBar
import com.willfp.eco.core.actionbar.PersistentActionBars
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.InjectablePlaceholder
import com.willfp.eco.core.placeholder.PlaceholderInjectable
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.namespacedKeyOf
import com.willfp.eco.util.toComponent
import com.willfp.ecoskills.plugin
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

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
    this.sendActionBar(message.toComponent())
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
