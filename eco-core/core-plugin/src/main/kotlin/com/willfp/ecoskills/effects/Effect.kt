package com.willfp.ecoskills.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener

abstract class Effect(
    id: String
): SkillObject(id), Listener {
    protected val plugin: EcoSkillsPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey
    val config: Config

    init {
        update()
        key = plugin.namespacedKeyFactory.create(id)
        config = plugin.effectsYml.getSubsection(id)

        Effects.registerNewEffect(this)
    }

    abstract fun formatDescription(string: String, level: Int): String

    fun getDescription(level: Int): String {
        val desc = config.getString("description", false)
        return StringUtils.format(formatDescription(desc, level))
    }

    fun update() {
        PlaceholderEntry(
            id,
            { player -> player.getEffectLevel(this).toString() },
            true
        ).register()

        PlaceholderEntry(
            "${id}_numeral",
            { player -> NumberUtils.toNumeral(player.getEffectLevel(this)) },
            true
        ).register()

        PlaceholderEntry(
            "${id}_description",
            { player -> this.getDescription(player.getEffectLevel(this)) },
            true
        ).register()
    }
}