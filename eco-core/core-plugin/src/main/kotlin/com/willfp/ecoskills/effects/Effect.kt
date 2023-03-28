package com.willfp.ecoskills.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.getEffectLevel
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.conditions.Conditions
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.UUID

abstract class Effect(
    id: String
) : SkillObject(id), Listener {
    private lateinit var conditions: ConditionList

    constructor(
        id: String,
        forceConfig: Config
    ) : this(id) {
        this.config = forceConfig
    }

    protected val plugin: EcoSkillsPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey = plugin.namespacedKeyFactory.create(id)
    val dataKey = PersistentDataKey(
        plugin.namespacedKeyFactory.create(id),
        PersistentDataKeyType.INT,
        0
    )
    val uuid: UUID = UUID.nameUUIDFromBytes(id.toByteArray())
    lateinit var config: Config

    init {
        finishLoading()
    }

    private fun finishLoading() {
        if (!::config.isInitialized) {
            config = loadConfig()
        }

        conditions = Conditions.compile(
            this.config.getSubsections("conditions"),
            ViolationContext(plugin, "Effect $id")
        )

        Effects.registerNewEffect(this)
        update()
    }

    open fun loadConfig(): Config {
        return plugin.effectsYml.getSubsection(id)
    }

    abstract fun formatDescription(string: String, level: Int): String

    fun getDescription(level: Int): String {
        val desc = config.getString("description")
        return StringUtils.format(formatDescription(desc, level))
    }

    fun update() {
        PlayerPlaceholder(
            plugin,
            id
        ) { player -> player.getEffectLevel(this).toString() }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_numeral"
        ) { player -> NumberUtils.toNumeral(player.getEffectLevel(this)) }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_description"
        ) { player -> this.getDescription(player.getEffectLevel(this)) }.register()
    }

    protected fun checkConditions(player: Player): Boolean {
        return conditions.areMet(player)
    }
}
