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
        dataYml.set("player.$uuid.$key", value)
    }

    override fun <T> read(uuid: UUID, key: String): T? {
        return dataYml.get("player.$uuid.$key") as T?
    }

    class DataYml(
        plugin: EcoSkillsPlugin
    ) : YamlBaseConfig(
        "data",
        false,
        plugin
    )
}