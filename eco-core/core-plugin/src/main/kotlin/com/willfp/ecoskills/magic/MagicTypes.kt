package com.willfp.ecoskills.magic

import wiki.ecohub.config.Config
import wiki.ecohub.config.category.RegistrableCategory
import wiki.ecohub.core.plugin.EcoPlugin
import wiki.ecohub.core.price.Prices

object MagicTypes : RegistrableCategory<MagicType>("magic_type", "magic_types") {
    override val supportsSharing = false

    override fun clear(plugin: EcoPlugin) {
        for (type in registry.values()) {
            Prices.unregisterPriceFactory(type.priceFactory)
        }
        registry.clear()
    }

    override fun acceptConfig(plugin: EcoPlugin, id: String, config: Config) {
        registry.register(MagicType(id, config))
    }
}
