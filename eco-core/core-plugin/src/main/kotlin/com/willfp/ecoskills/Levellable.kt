package com.willfp.ecoskills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toNiceString
import com.willfp.ecoskills.util.LevelInjectable
import com.willfp.ecoskills.util.loadDescriptionPlaceholders
import org.bukkit.OfflinePlayer
import java.util.concurrent.TimeUnit

abstract class Levellable(
    final override val id: String,
    val config: Config
) : KRegistrable {
    val startLevel = 0

    private val key = PersistentDataKey(
        plugin.createNamespacedKey(id),
        PersistentDataKeyType.INT,
        startLevel
    )

    private val descCache = Caffeine.newBuilder()
        .expireAfterWrite(plugin.configYml.getInt("gui.cache-ttl").toLong(), TimeUnit.MILLISECONDS)
        .build<Int, String>()

    private val unformattedDescription = config.getString("description")

    val name = config.getFormattedString("name")

    init {
        Placeholders.applyInternalLevellablePlaceholders(this)
        Placeholders.applyExternalLevellablePlaceholders(this)
    }

    internal open fun getActualLevel(player: OfflinePlayer) = getSavedLevel(player)

    internal fun getSavedLevel(player: OfflinePlayer) = player.profile.read(key)
    internal fun setSavedLevel(player: OfflinePlayer, level: Int) = player.profile.write(key, level)

    fun getDescription(level: Int): String {
        descCache.getIfPresent(level)?.let { return it }

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
