package com.willfp.ecoskills.data.legacy

import com.willfp.eco.core.config.yaml.YamlBaseConfig
import com.willfp.ecoskills.EcoSkillsPlugin
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class YamlDataHandler(
    plugin: EcoSkillsPlugin
) : DataHandler {
    private val dataYml = DataYml(plugin)

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