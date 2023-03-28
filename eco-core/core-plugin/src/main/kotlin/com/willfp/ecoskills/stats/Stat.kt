package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.getBaseStatLevel
import com.willfp.ecoskills.getBonusStatLevel
import com.willfp.ecoskills.getStatLevel
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

abstract class Stat(
    id: String
) : SkillObject(id), Listener, Registrable {
    constructor(
        id: String,
        forceConfig: Config
    ) : this(id) {
        this.config = forceConfig
    }

    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey = plugin.namespacedKeyFactory.create(id)
    val uuid: UUID = UUID.nameUUIDFromBytes(id.toByteArray())
    val dataKey = PersistentDataKey(
        plugin.namespacedKeyFactory.create(id),
        PersistentDataKeyType.INT,
        0
    )
    lateinit var config: Config
    lateinit var name: String

    init {
        finishLoading()
    }

    private fun finishLoading() {
        if (!::config.isInitialized) {
            config = loadConfig()
        }

        Stats.registerNewStat(this)

        update()
    }

    open fun loadConfig(): Config {
        return plugin.configYml.getSubsection("stats.$id")
    }

    open fun formatDescription(string: String, level: Int): String {
        return string
    }

    fun getDescription(level: Int): String {
        val desc = config.getString("description")
        return StringUtils.format(formatDescription(desc, level))
    }

    fun update() {
        name = config.getFormattedString("name")

        PlayerPlaceholder(
            plugin,
            id
        ) { player -> player.getStatLevel(this).toString() }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_description"
        ) { player -> this.getDescription(player.getStatLevel(this)) }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_numeral"
        ) { player -> NumberUtils.toNumeral(player.getStatLevel(this)) }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_base"
        ) { player -> player.getBaseStatLevel(this).toString() }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_base_numeral"
        ) { player -> NumberUtils.toNumeral(player.getBaseStatLevel(this)) }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_bonus"
        ) { player ->
            val bonus = player.getBonusStatLevel(this)
            return@PlayerPlaceholder when {
                bonus > 0 -> "+$bonus"
                bonus < 0 -> "$bonus"
                else -> ""
            }
        }.register()

        PlayerlessPlaceholder(
            plugin,
            "${id}_name"
        ) { this.name }.register()
    }

    open fun updateStatLevel(player: Player) {
        // Override when needed.
    }
}