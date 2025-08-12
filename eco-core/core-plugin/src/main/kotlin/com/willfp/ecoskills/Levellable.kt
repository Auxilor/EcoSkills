package com.willfp.ecoskills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerStaticPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.eco.util.toNumeral
import com.willfp.ecoskills.util.LeaderboardEntry
import com.willfp.ecoskills.util.LevelInjectable
import com.willfp.ecoskills.util.loadDescriptionPlaceholders
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.UUID
import java.util.concurrent.TimeUnit

abstract class Levellable(
    final override val id: String,
    val config: Config,
    protected val plugin: EcoSkillsPlugin
) : KRegistrable {
    val startLevel = 0

    private val key = PersistentDataKey(
        plugin.createNamespacedKey(id),
        PersistentDataKeyType.INT,
        startLevel
    )

    // Not the best way to do this, but it works!
    private val leaderboardCache = Caffeine.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build<Boolean, List<UUID>> {
            Bukkit.getOfflinePlayers().sortedByDescending {
                getActualLevel(it)
            }.map { it.uniqueId }
        }

    fun getPosition(uuid: UUID): Int? {
        val leaderboard = leaderboardCache.get(true)
        val index = leaderboard.indexOf(uuid)
        return if (index == -1) null else index + 1
    }

    private val descCache = Caffeine.newBuilder()
        .expireAfterWrite(plugin.configYml.getInt("gui.cache-ttl").toLong(), TimeUnit.MILLISECONDS)
        .build<Int, String>()

    private val unformattedDescription = config.getString("description")

    val name = config.getFormattedString("name")

    init {
        config.injectPlaceholders(
            PlayerStaticPlaceholder("level") {
                getActualLevel(it).toString()
            }
        )

        PlayerPlaceholder(plugin, id) {
            getActualLevel(it).toString()
        }.register()

        PlayerPlaceholder(plugin, "${id}_numeral") {
            getActualLevel(it).toNumeral()
        }.register()

        PlayerlessPlaceholder(plugin, "${id}_name") {
            name
        }.register()

        PlayerPlaceholder(plugin, "${id}_description") {
            getDescription(getActualLevel(it))
        }.register()

        PlayerPlaceholder(plugin, "${id}_leaderboard_rank") { player ->
            val emptyPosition = plugin.langYml.getString("top.empty-position")
            val position = getPosition(player.uniqueId)
            position?.toString() ?: emptyPosition
        }.register()
    }

    internal open fun getActualLevel(player: OfflinePlayer) = getSavedLevel(player)

    internal fun getSavedLevel(player: OfflinePlayer) = player.profile.read(key)
    internal fun setSavedLevel(player: OfflinePlayer, level: Int) = player.profile.write(key, level)

    fun addPlaceholdersInto(string: String, level: Int): String {
        var result = string
            .replace("%ecoskills_${id}_numeral%", level.toNumeral())
            .replace("%ecoskills_${id}_description%", getDescription(level))
            .replace("%ecoskills_${id}%", level.toString())
            .replace("%level%", level.toString())
            .replace("%level_numeral%", level.toNumeral())

        // Regex for %level_X% and %level_X_numeral%
        val regex = Regex("%level_(-?\\d+)(_numeral)?%")

        result = regex.replace(result) { match ->
            val offset = match.groupValues[1].toIntOrNull() ?: return@replace match.value
            val isNumeral = match.groupValues[2].isNotEmpty()
            val newLevel = level + offset

            if (isNumeral) newLevel.toNumeral() else newLevel.toString()
        }

        return result
    }

    fun getTop(position: Int): LeaderboardEntry? {
        require(position > 0) { "Position must be greater than 0" }

        val uuid = leaderboardCache.get(true).getOrNull(position - 1) ?: return null

        val player = Bukkit.getOfflinePlayer(uuid).takeIf { it.hasPlayedBefore() } ?: return null

        return LeaderboardEntry(
            player,
            getActualLevel(player)
        )
    }

    fun getDescription(level: Int): String {
        return descCache.get(level) {
            var desc = unformattedDescription

            val context = placeholderContext(
                injectable = LevelInjectable(level)
            )

            for (placeholder in loadDescriptionPlaceholders(config)) {
                val id = placeholder.id
                val value = evaluateExpression(placeholder.expr, context)
                desc = desc.replace("%$id%", value.toNiceString())
            }

            desc.formatEco(context)
        }
    }
}
