package com.willfp.ecoskills.stats

import com.willfp.ecoskills.api.averageSkillLevel
import com.willfp.ecoskills.api.totalSkillLevel
import com.willfp.ecoskills.gui.menus.StatsGUI
import wiki.ecohub.config.Config
import wiki.ecohub.config.category.RegistrableCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.stdlib.toNiceString
import wiki.ecohub.ui.placeholder.type.PlayerPlaceholder

object Stats : RegistrableCategory<Stat>("stat", "stats") {
    override val supportsSharing = false

    override fun beforeReload(plugin: EcoPlugin) {
        PlayerPlaceholder(plugin, "total_skill_level") {
            it.totalSkillLevel.toNiceString()
        }.register()

        PlayerPlaceholder(plugin, "average_skill_level") {
            it.averageSkillLevel.toNiceString()
        }.register()
    }

    override fun clear(plugin: EcoPlugin) {
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(Stat(id, config))
    }

    override fun afterReload(plugin: EcoPlugin) {
        StatsGUI.update()
    }
}
