package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.*
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

abstract class Stat(
    id: String
) : SkillObject(id), Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey = plugin.namespacedKeyFactory.create(id)
    val uuid: UUID = UUID.nameUUIDFromBytes(id.toByteArray())
    val dataKey = PersistentDataKey(
        plugin.namespacedKeyFactory.create(id),
        PersistentDataKeyType.INT,
        0
    ).player()
    lateinit var config: Config
    lateinit var name: String

    init {
        finishLoading()
    }

    private fun finishLoading() {
        config = loadConfig()

        Stats.registerNewStat(this)

        update()
    }

    open fun loadConfig(): Config {
        return plugin.configYml.getSubsection("stats.$id")
    }

    fun update() {
        name = config.getFormattedString("name")

        PlayerPlaceholder(
            plugin,
            id
        ) { player -> player.getStatLevel(this).toString() }.register()

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