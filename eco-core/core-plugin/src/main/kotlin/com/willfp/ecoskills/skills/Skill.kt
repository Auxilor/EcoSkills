package com.willfp.ecoskills.skills

import com.google.gson.annotations.Since
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoskills.EcoSkillsPlugin
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

abstract class Skill(
    val id: String
): Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey
    val xpKey: NamespacedKey
    val uuid: UUID
    val config: Config
    lateinit var name: String

    init {
        key = plugin.namespacedKeyFactory.create(id)
        xpKey = plugin.namespacedKeyFactory.create(id + "_progress")
        uuid = UUID.nameUUIDFromBytes(id.toByteArray())
        config = plugin.configYml.getSubsection("skills.$id")

        Skills.registerNewSkill(this)
    }

    fun update() {
        name = plugin.langYml.getString("skills.$id.name")

        postUpdate()
    }

    open fun postUpdate() {
        // Override when needed
    }

    fun getExpForLevel(level: Int): Int {
        return this.plugin.configYml.getInts("skills.level-xp-requirements")[level - 1] ?: Integer.MAX_VALUE
    }
}