package com.willfp.ecoskills.actionbar

import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.commands.CommandToggleActionbar
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import java.util.UUID

object ActionBarUtils {
    private val blacklist = mutableMapOf<UUID, Long>()
    private val whitelist = mutableMapOf<UUID, Long>()
    private val plugin = EcoSkillsPlugin.getInstance()

    val ecoSkillsComponentSignature = StringUtils.toComponent(
        "§t" // Literally a random character
    )

    fun blacklist(uuid: UUID) {
        if (isWhitelisted(uuid)) {
            return
        }
        blacklist[uuid] = System.currentTimeMillis() + 3000
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
        plugin.scheduler.runTimer({
            for (player in Bukkit.getOnlinePlayers()) {
                if (isBlacklisted(player.uniqueId)) {
                    continue
                }

                if (!PlayerProfile.load(player).read(CommandToggleActionbar.DESCRIPTIONS_KEY)) {
                    continue
                }

                val message = plugin.configYml
                    .getString("persistent-action-bar.format", false)
                val component = StringUtils.formatToComponent(message, player)
                    .append(ecoSkillsComponentSignature)

                whitelistTemp(player.uniqueId)
                player.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    *TextComponent.fromLegacyText(StringUtils.toLegacy(component))
                )
            }
        }, 5, 5)
    }
}