package com.willfp.ecoskills.actionbar

import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import java.util.UUID

object ActionBarUtils {
    private val blacklist = mutableSetOf<UUID>()
    private val plugin = EcoSkillsPlugin.getInstance()

    val ecoSkillsComponentSignature = StringUtils.toComponent(
        "§t" // Literally a random character
    )

    fun blacklist(uuid: UUID) {
        blacklist.add(uuid)
        plugin.scheduler.runLater({
            unBlacklist(uuid)
        }, 60)
    }

    fun unBlacklist(uuid: UUID) {
        blacklist.remove(uuid)
    }

    fun isBlacklisted(uuid: UUID): Boolean {
        return blacklist.contains(uuid)
    }

    @JvmStatic
    fun startRunnable() {
        plugin.scheduler.runTimer({
            for (player in Bukkit.getOnlinePlayers()) {
                if (isBlacklisted(player.uniqueId)) {
                    continue
                }

                val message = plugin.configYml
                    .getString("persistent-action-bar.format", false)
                val component = StringUtils.formatToComponent(message, player)
                    .append(ecoSkillsComponentSignature)

                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    *TextComponent.fromLegacyText(StringUtils.toLegacy(component))
                )
            }
        }, 5, 5)
    }
}