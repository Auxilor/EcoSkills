package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.getStatLevel
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
        name = config.getString("name")

        PlaceholderEntry(
            id,
            { player -> player.getStatLevel(this).toString() },
            true
        ).register()

        PlaceholderEntry(
            "${id}_numeral",
            { player -> NumberUtils.toNumeral(player.getStatLevel(this)) },
            true
        ).register()

        PlaceholderEntry(
            "${id}_name",
            { this.name },
            false
        ).register()
    }

    open fun updateStatLevel(player: Player) {
        // Override when needed.
    }
}