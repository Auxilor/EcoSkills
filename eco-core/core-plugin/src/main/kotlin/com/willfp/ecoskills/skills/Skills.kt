package com.willfp.ecoskills.skills

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.leaderboard.Leaderboard
import com.willfp.eco.core.leaderboard.LeaderboardEntry
import com.willfp.eco.core.leaderboard.Leaderboards
import com.willfp.eco.core.leaderboard.registerStandardPlaceholders
import com.willfp.eco.core.leaderboard.registerTopPlaceholders
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
        // Nothing at all is registered when disabled -- no leaderboard, and no placeholders,
        // matching every per-skill leaderboard.
        if (!plugin.configYml.getBool("leaderboard.enabled")) {
            leaderboard = null
            return
        }

        // A total across every skill cannot be expressed as a single key, so this stays a custom
        // provider and is refreshed by the reconcile sweep rather than incrementally.
        val leaderboard = Leaderboards.register(plugin, "total_skill_level") { uuids ->
            val skills = values()

            // A player who is at the start level in every skill has made no progress, exactly as
            // a player at the start level of one skill has made none on that skill's leaderboard.
            // Ranking them would pad the leaderboard with the whole playerbase and shrink every
            // percentile, so the sum of the defaults is the threshold rather than zero.
            val noProgress = skills.sumOf { it.key.defaultValue.toDouble() }

            // One batched read for every skill key at once, rather than a separate pass over the
            // table per skill.
            val stored = Eco.get().readAllProfileValuesForKeys(uuids, skills.map { it.key })

            val totals = HashMap<UUID, Double>()

            for (skill in skills) {
                val default = skill.key.defaultValue.toDouble()
                val levels = stored[skill.key].orEmpty()

                for (uuid in uuids) {
                    // Absent means the player is on this skill's default, which still counts
                    // toward their total -- it is the total that decides whether they are ranked.
                    val level = (levels[uuid] as? Number)?.toDouble() ?: default

                    totals.merge(uuid, level, Double::plus)
                }
            }

            totals.filterValues { it > noProgress }
        }

        this.leaderboard = leaderboard

        leaderboard.registerStandardPlaceholders(
            plugin,
            "leaderboard",
            plugin.langYml.getString("top.empty-position")
        ) { it.toInt().toString() }

        leaderboard.registerTopPlaceholders(
            plugin,
            plugin.langYml.getString("top.empty-position"),
            listOf("level", "amount")
        )
    }

    fun registerPlaceholders() {
        // Only registered when leaderboards are on, matching every other leaderboard placeholder:
        // with them off, nothing registers at all rather than resolving to the empty position.
        // registerLeaderboard() replaces this by name when it runs, so the two cannot conflict.
        if (!plugin.configYml.getBool("leaderboard.enabled")) {
            return
        }

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
