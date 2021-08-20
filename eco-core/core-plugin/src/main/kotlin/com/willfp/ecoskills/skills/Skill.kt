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

    fun getExpForLevel(level: Int): Int {
        val level1xp = this.plugin.configYml.getInt("skills.level-1-xp")
        val multiplier = this.plugin.configYml.getDouble("skills.xp-multiplier-per-level")
        val sigFig = this.plugin.configYml.getInt("skills.sig-fig")
        val roundTo = this.plugin.configYml.getInt("skills.round-to")

        var xp = level1xp * (level.toDouble().pow(multiplier))
        val bigDecimal = BigDecimal(xp)
        bigDecimal.round(MathContext(sigFig))
        xp = bigDecimal.toDouble()
        return ((xp / roundTo).roundToInt() * roundTo)
    }
}