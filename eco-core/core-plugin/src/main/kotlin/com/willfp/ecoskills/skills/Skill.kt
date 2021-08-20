package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.NamespacedKey
import java.util.*

abstract class Skill(
    val id: String
) {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey
    val xpKey: NamespacedKey
    val uuid: UUID
    val config: Config
    lateinit var name: String

    init {
        update()
        key = plugin.namespacedKeyFactory.create(id)
        xpKey = plugin.namespacedKeyFactory.create(id + "_progress")
        uuid = UUID.nameUUIDFromBytes(id.toByteArray())
        config = plugin.configYml.getSubsection("stats.$id")

        Skills.registerNewSkill(this)
    }

    fun update() {
        name = plugin.langYml.getString("skills.$id.name")
    }
}