package com.willfp.ecoskills.data.storage

import com.willfp.ecoskills.EcoSkillsPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    private val plugin: EcoSkillsPlugin
) : DataHandler {
    init {
        Database.connect(
            "jdbc:mysql://" +
                    "${plugin.configYml.getString("mysql.host")}:" +
                    "${plugin.configYml.getString("mysql.port")}/" +
                    plugin.configYml.getString("mysql.database"),
            driver = "com.mysql.jdbc.Driver",
            user = plugin.configYml.getString("mysql.user"),
            password = plugin.configYml.getString("mysql.password")
        )

        transaction {
            SchemaUtils.create(Players)
        }
    }

    override fun save() {
        TODO()
    }

    override fun <T> write(uuid: UUID, key: String, value: T) {
        transaction {
            Players.insert {
                it[id] = id.toString()
            }
        }
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

    object Players: Table() {
        val id = varchar("uuid", 36)
    }
}