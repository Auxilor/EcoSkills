package com.willfp.ecoskills.stats

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoskills.skills.Skills
import org.bukkit.NamespacedKey

class Stat(
    protected val plugin: EcoPlugin,
    val id: String
) {
    val key: NamespacedKey
    lateinit var icon: String
    lateinit var color: String
    lateinit var name: String

    init {
        update()
        key = plugin.namespacedKeyFactory.create(id)

        Stats.registerNewStat(this)
    }

    fun update() {
        icon = plugin.langYml.getString("stats.icons.$id")
        name = plugin.langYml.getString("stats.names.$id")
        color = plugin.langYml.getString("stats.colors.$id")
    }
}