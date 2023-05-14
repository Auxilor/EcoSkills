package com.willfp.ecoskills

import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.loader.configs.ConfigCategory

abstract class CategoryWithRegistry<T : Registrable>(
    id: String,
    directory: String
) : ConfigCategory(id, directory) {
    protected val registry = Registry<T>()

    fun getByID(id: String?): T? = id?.let { registry[id] }

    fun values(): Set<T> = registry.values()
}
