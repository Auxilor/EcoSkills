package com.willfp.ecoskills.skills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.api.totalSkillLevel
import com.willfp.ecoskills.gui.menus.SkillsGUI
import com.willfp.ecoskills.util.InvalidConfigurationException
import com.willfp.ecoskills.util.LeaderboardEntry
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory
import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.TimeUnit

object Skills : RegistrableCategory<Skill>("skill", "skills") {
    // Totally not copied over from Levellable
    private val leaderboardCache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build<Boolean, List<UUID>> {
            Bukkit.getOfflinePlayers().sortedByDescending {
                it.totalSkillLevel
            }.map { it.uniqueId }
        }

    fun getTop(position: Int): LeaderboardEntry? {
        require(position > 0) { "Position must be greater than 0" }

        val uuid = leaderboardCache.get(true).getOrNull(position - 1) ?: return null

        val player = Bukkit.getOfflinePlayer(uuid).takeIf { it.hasPlayedBefore() } ?: return null

        return LeaderboardEntry(
            player,
            player.totalSkillLevel
        )
    }

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        try {
            registry.register(Skill(id, config, plugin as EcoSkillsPlugin))
        } catch (e: InvalidConfigurationException) {
            plugin.logger.warning("Failed to load skill $id: ${e.message}")
        }
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        SkillsGUI.update(plugin)
    }
}
