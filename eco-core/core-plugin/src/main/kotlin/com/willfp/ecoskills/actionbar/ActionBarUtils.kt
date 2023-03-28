@file:Suppress("DEPRECATION")

package com.willfp.ecoskills.actionbar

import com.willfp.eco.core.data.profile
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.commands.CommandToggleActionbar
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import java.util.UUID

object ActionBarUtils {
    private val blacklist = mutableMapOf<UUID, Long>()
    private val whitelist = mutableMapOf<UUID, Long>()
    private val plugin = EcoSkillsPlugin.getInstance()

    fun blacklist(uuid: UUID) {
        if (isWhitelisted(uuid)) {
            return
        }
        blacklist[uuid] = System.currentTimeMillis() + plugin.configYml.getInt("persistent-action-bar.reset-time")
    }

    private fun isBlacklisted(uuid: UUID): Boolean {
        val endTime = blacklist[uuid] ?: return false
        return endTime > System.currentTimeMillis()
    }

    private fun whitelistTemp(uuid: UUID) {
        whitelist[uuid] = System.currentTimeMillis() + 50
    }

    private fun isWhitelisted(uuid: UUID): Boolean {
        val endTime = whitelist[uuid] ?: return false
        return endTime > System.currentTimeMillis()
    }

    @JvmStatic
    fun startRunnable() {
        plugin.scheduler.runTimer(
            {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (isBlacklisted(player.uniqueId)) {
                        continue
                    }

                    if (plugin.configYml.getStrings("persistent-action-bar.disabled-in-worlds")
                            .map { it.lowercase() }
                            .contains(player.world.name.lowercase())
                    ) {
                        continue
                    }

                    if (!player.profile.read(CommandToggleActionbar.DESCRIPTIONS_KEY)) {
                        continue
                    }

                    if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
                        continue
                    }

                    if (plugin.configYml.getBool("persistent-action-bar.scale-health")) {
                        player.isHealthScaled = true
                        player.healthScale = 20.0
                    }

                    val message = plugin.configYml
                        .getString("persistent-action-bar.format")
                    val component = StringUtils.format(message, player)

                    whitelistTemp(player.uniqueId)
                    player.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        *TextComponent.fromLegacyText(component)
                    )
                }
            },
            plugin.configYml.getInt("persistent-action-bar.update-time").toLong(),
            plugin.configYml.getInt("persistent-action-bar.update-time").toLong()
        )
    }
}