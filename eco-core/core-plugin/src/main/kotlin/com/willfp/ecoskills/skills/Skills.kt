package com.willfp.ecoskills.skills

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.leaderboard.Leaderboard
import com.willfp.eco.core.leaderboard.LeaderboardEntry
import com.willfp.eco.core.leaderboard.Leaderboards
import com.willfp.eco.core.leaderboard.registerStandardPlaceholders
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.ecoskills.gui.menus.SkillsGUI
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.util.InvalidConfigurationException
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.RegistrableCategory
import java.util.UUID

object Skills : RegistrableCategory<Skill>("skill", "skills") {
    /**
     * The leaderboard ranking players by their total skill level, or null before the first
     * reload has registered it.
     */
    var leaderboard: Leaderboard? = null
        private set

    fun getTop(position: Int): LeaderboardEntry? {
        require(position > 0) { "Position must be greater than 0" }

        return leaderboard?.getTop(position)
    }

    fun getPosition(uuid: UUID): Int? {
        return leaderboard?.getPosition(uuid)
    }

    /**
     * Register (or re-register) the total skill level leaderboard and its placeholders.
     *
     * Called from the plugin's reload handler, after every skill has been loaded.
     */
    internal fun registerLeaderboard() {
        val enabled = plugin.configYml.getBool("leaderboard.enabled")

        val leaderboard = Leaderboards.register(plugin, "total_skill_level") { uuids ->
            if (!enabled) {
                emptyMap()
            } else {
                val totals = HashMap<UUID, Double>()

                // One bulk read per skill, rather than one profile read per player per skill.
                for (skill in values()) {
                    for ((uuid, level) in Eco.get().readAllProfileValues(uuids, skill.key)) {
                        totals.merge(uuid, level.toDouble(), Double::plus)
                    }
                }

                // A player with no level saved for any skill still totals zero, and was still
                // ranked by the old leaderboard, so they must stay ranked here.
                for (uuid in uuids) {
                    totals.putIfAbsent(uuid, 0.0)
                }

                totals
            }
        }

        this.leaderboard = leaderboard

        if (enabled) {
            leaderboard.registerStandardPlaceholders(
                plugin,
                "leaderboard",
                plugin.langYml.getString("top.empty-position")
            ) { it.toInt().toString() }
        }
    }

    fun registerPlaceholders() {
        PlayerPlaceholder(plugin, "leaderboard_rank") { player ->
            val emptyPosition = plugin.langYml.getString("top.empty-position")
            val position = getPosition(player.uniqueId)
            position?.toString() ?: emptyPosition
        }.register()
    }

    override fun clear(plugin: LibreforgePlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: LibreforgePlugin, id: String, config: Config) {
        try {
            registry.register(Skill(id, config))
        } catch (e: InvalidConfigurationException) {
            plugin.logger.warning("Failed to load skill $id: ${e.message}")
        }
    }

    override fun afterReload(plugin: LibreforgePlugin) {
        SkillsGUI.update()
    }
}
