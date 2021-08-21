package com.willfp.ecoskills.effects

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.getEffectLevel
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import java.util.*

abstract class Effect(
    id: String
): SkillObject(id), Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey
    val uuid: UUID
    val config: Config
    lateinit var name: String
    lateinit var description: String

    init {
        update()
        key = plugin.namespacedKeyFactory.create(id)
        uuid = UUID.nameUUIDFromBytes(id.toByteArray())
        config = plugin.configYml.getSubsection("effects.$id")

        Effects.registerNewEffect(this)
    }

    fun update() {
        name = plugin.langYml.getString("effects.$id.name")
        description = plugin.langYml.getString("effects.$id.description")

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
    }
}