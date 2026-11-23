package com.willfp.ecoskills

import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.util.LevelInjectable
import com.willfp.ecoskills.util.loadDescriptionPlaceholders
import org.bukkit.OfflinePlayer
import wiki.ecohub.config.Config
import wiki.ecohub.core.registry.KRegistrable
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.data.PersistentDataKey
import wiki.ecohub.data.PersistentDataKeyType
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.entity.player.profile
import wiki.ecohub.math.evaluateExpression
import wiki.ecohub.math.toNumeral
import wiki.ecohub.modules.progression.ProgressionPlaceholders
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.placeholder.type.PlayerPlaceholder
import wiki.ecohub.ui.placeholder.type.PlayerStaticPlaceholder
import wiki.ecohub.ui.placeholder.type.PlayerlessPlaceholder
import wiki.ecohub.ui.text.formatEco
import java.time.Duration

abstract class Levellable(
    final override val id: String,
    val config: Config
) : KRegistrable {
    val startLevel = 0

    internal val key = PersistentDataKey(
        plugin.createNamespacedKey(id),
        PersistentDataKeyType.INT,
        startLevel
    )

    private val descCache = EcoCache.builder<Int, String>()
        .expireAfterWrite(Duration.ofMillis(plugin.configYml.getInt("gui.cache-ttl").toLong()))
        .build()

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

        // Only skills have a leaderboard; stats resolve to the empty position, as they always have.
        PlayerPlaceholder(plugin, "${id}_leaderboard_rank") { player ->
            val emptyPosition = plugin.langYml.getString("top.empty-position").formatEco()
            val position = (this as? Skill)?.leaderboard?.getPosition(player.uniqueId)
            position?.toString() ?: emptyPosition
        }.register()
    }

    internal open fun getActualLevel(player: OfflinePlayer) = getSavedLevel(player)

    internal fun getSavedLevel(player: OfflinePlayer) = player.profile.read(key)
    internal fun setSavedLevel(player: OfflinePlayer, level: Int) = player.profile.write(key, level)

    fun addPlaceholdersInto(string: String, level: Int): String {
        val result = string
            .replace("%ecoskills_${id}_numeral%", level.toNumeral())
            .replace("%ecoskills_${id}_description%", getDescription(level))
            .replace("%ecoskills_${id}%", level.toString())

        // %level%, %level_numeral%, %previous_level%, %previous_level_numeral% and the
        // %level_N% / %level_N_numeral% offsets, resolved by the shared helper in eco so a
        // lore line and an effect chain can never disagree about what %level_2% means.
        return ProgressionPlaceholders.inject(result, "level", level)
    }

    fun getDescription(level: Int): String {
        descCache[level]?.let { return it }

        var desc = unformattedDescription

        val context = placeholderContext(
            injectable = LevelInjectable(level)
        )

        for (placeholder in loadDescriptionPlaceholders(config)) {
            val id = placeholder.id
            val value = evaluateExpression(placeholder.expr, context)
            desc = desc.replace("%$id%", value.toNiceString())
        }

        val result = desc.formatEco(context)
        descCache.put(level, result)
        return result
    }
}
