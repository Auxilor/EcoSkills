package com.willfp.ecoskills.data.legacy

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class MySQLDataHandler(
    plugin: EcoSkillsPlugin
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
                    registerColumn<Double>(skill.id + "_progress", DoubleColumnType())
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

    override fun <T> read(uuid: UUID, key: String): T? {
        var value: T? = null
        transaction {
            val player = Players.select { Players.id eq uuid }.firstOrNull() ?: return@transaction
            value = player[Players.columns.stream().filter { it.name == key }.findFirst().get()] as T?
        }
        return value
    }

    object Players : UUIDTable("EcoSkills_Players") {
        override val id: Column<EntityID<UUID>> = uuid("uuid")
            .entityId()
    }
}