package com.willfp.ecoskills.data.storage

import com.willfp.ecoskills.EcoSkillsPlugin
import java.util.*

@Suppress("UNCHECKED_CAST")
class SQLDataHandler(
    private val plugin: EcoSkillsPlugin
) : DataHandler {
    override fun save() {
        TODO()
    }

    override fun <T> write(uuid: UUID, key: String, value: T) {
        TODO()
    }

    override fun readInt(uuid: UUID, key: String): Int {
        TODO()
    }

    override fun readDouble(uuid: UUID, key: String): Double {
        TODO()
    }

    override fun readString(uuid: UUID, key: String, default: String): String {
        TODO()
    }
}