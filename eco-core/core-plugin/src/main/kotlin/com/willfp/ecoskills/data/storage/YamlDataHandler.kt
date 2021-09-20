package com.willfp.ecoskills.data.storage

import com.willfp.eco.core.config.yaml.YamlBaseConfig
import com.willfp.ecoskills.EcoSkillsPlugin
import java.util.*

@Suppress("UNCHECKED_CAST")
class YamlDataHandler(
    private val plugin: EcoSkillsPlugin
) : DataHandler {
    private val dataYml = DataYml(plugin)

    override fun save() {
        dataYml.save()
    }

    override fun <T> write(uuid: UUID, key: String, value: T) {
        dataYml.set("player.$uuid.key", value)
    }

    override fun readInt(uuid: UUID, key: String): Int {
        return dataYml.getInt("player.$uuid.$key", 0)
    }

    override fun readDouble(uuid: UUID, key: String): Double {
        return dataYml.getDoubleOrNull("player.$uuid.$key") ?: 0.0
    }

    override fun readString(uuid: UUID, key: String, default: String): String {
        return dataYml.getStringOrNull("player.$uuid.$key") ?: default
    }

    class DataYml(
        plugin: EcoSkillsPlugin
    ) : YamlBaseConfig(
        "data",
        false,
        plugin
    )
}