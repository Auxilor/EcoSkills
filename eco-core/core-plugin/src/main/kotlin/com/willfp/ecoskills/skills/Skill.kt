package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import org.bukkit.NamespacedKey

class Skill(
    protected val plugin: EcoPlugin,
    val id: String
) {
    val key: NamespacedKey
    lateinit var name: String

    init {
        update()
        key = plugin.namespacedKeyFactory.create(id)

        Skills.registerNewSkill(this)
    }

    fun update() {
        name = plugin.langYml.getString("skills.names.$id")
    }
}