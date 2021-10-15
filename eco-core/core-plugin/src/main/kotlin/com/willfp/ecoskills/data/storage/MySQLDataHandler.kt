package com.willfp.ecoskills.data.storage

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
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
            driver = "com.mysql.cj.jdbc.Driver",
            user = plugin.configYml.getString("mysql.user"),
            password = plugin.configYml.getString("mysql.password")
        )

        transaction {
            Players.apply {
                for (skill in Skills.values()) {
                    registerColumn<Int>(skill.id, IntegerColumnType())
                        .default(0)
                    registerColumn<Double>(skill.xpKey.key, DoubleColumnType())
                        .default(0.0)
                }

                for (stat in Stats.values()) {
                    registerColumn<Int>(stat.id, IntegerColumnType())
                        .default(0)
                }

                for (effect in Effects.values()) {
                    registerColumn<Int>(effect.id, IntegerColumnType())
                        .default(0)
                }
            }

            SchemaUtils.create(Players)
        }
    }

    override fun save() {
        // Do nothing
    }

    override fun <T> write(uuid: UUID, key: String, value: T) {
        transaction {
            Players.select { Players.id eq uuid }.firstOrNull() ?: run {
                Players.insert {
                    it[this.id] = uuid
                }
            }
            val column: Column<T> = Players.columns.stream().filter { it.name == key }.findFirst().get() as Column<T>
            Players.update({ Players.id eq uuid }) {
                it[column] = value
            }
        }
    }

    override fun <T> read(uuid: UUID, key: String, default: T): T {
        var value = default
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            value = player[Players.columns.stream().filter { it.name == key }.findFirst().get()] as T? ?: default
        }
        return value
    }

    object Players : UUIDTable("EcoSkills_Players") {
        override val id: Column<EntityID<UUID>> = uuid("uuid")
            .entityId()
        val name = varchar("name", 50)
            .default("Unknown Player")
    }
}