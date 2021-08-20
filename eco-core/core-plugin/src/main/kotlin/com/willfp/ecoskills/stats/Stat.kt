package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

abstract class Stat(
    val id: String
) : Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey
    val uuid: UUID
    val config: Config
    lateinit var icon: String
    lateinit var color: String
    lateinit var name: String

    init {
        update()
        key = plugin.namespacedKeyFactory.create(id)
        uuid = UUID.nameUUIDFromBytes(id.toByteArray())
        config = plugin.configYml.getSubsection("stats.$id")

        Stats.registerNewStat(this)
    }

    fun update() {
        icon = plugin.langYml.getString("stats.$id.icon")
        name = plugin.langYml.getString("stats.$id.name")
        color = plugin.langYml.getString("stats.$id.color")
    }

    open fun updateStatLevel(player: Player) {
        // Override when needed.
    }
}