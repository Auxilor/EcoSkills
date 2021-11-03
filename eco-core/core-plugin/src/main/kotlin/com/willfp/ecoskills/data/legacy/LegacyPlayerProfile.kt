package com.willfp.ecoskills.data.legacy

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

@Suppress("UNCHECKED_CAST", "DEPRECATED")
class LegacyPlayerProfile private constructor(
    private val data: MutableMap<String, Any>
) {
    private fun <T : Any> read(key: String, default: T): T {
        return data[key] as T? ?: default
    }

    companion object {
        private val handler = EcoSkillsPlugin.getInstance().dataHandler
        private val loaded = mutableMapOf<UUID, LegacyPlayerProfile>()
        private val keys = mutableMapOf<String, Type>()
        private val mappedKeys = mutableMapOf<String, PersistentDataKey<*>>()

        private fun load(uuid: UUID): LegacyPlayerProfile {
            val found = loaded[uuid]
            if (found != null) {
                return found
            }

            val data = mutableMapOf<String, Any>()
            for ((key, type) in keys) {
                when (type) {
                    Type.INT -> data[key] = handler.read(uuid, key, 0)
                    Type.DOUBLE -> data[key] = handler.read(uuid, key, 0.0)
                }
            }

            val profile = LegacyPlayerProfile(data)
            loaded[uuid] = profile
            return profile
        }

        fun migrateAll() {
            if (plugin.configYml.getBool("mysql.migrated")) {
                return
            }
            plugin.logger.info("-----------------------------------------")
            plugin.logger.info("Migrating player data! (Automatic)")
            plugin.logger.info("This will only run once, ever - not every time your server starts up")
            plugin.logger.info("so there's no need to worry about how long it takes. Once this is complete,")
            plugin.logger.info("feel free to delete data.yml as it won't be used anymore")
            plugin.logger.info("-----------------------------------------")
            Bukkit.getLogger().info(keys.toString())
            Bukkit.getLogger().info(mappedKeys.toString())
            for (offlinePlayer in Bukkit.getServer().offlinePlayers) {
                plugin.logger.info("Migrating player ${offlinePlayer.uniqueId}...")
                migrate(offlinePlayer)
            }
            plugin.logger.info("Saving...")
            plugin.configYml.set("mysql.migrated", true)
            plugin.configYml.save()
            Eco.getHandler().playerProfileHandler.saveAll(false)
            plugin.logger.info("Migration complete!")
        }

        private fun migrate(player: OfflinePlayer) {
            fun <T> writeKey(profile: PlayerProfile, key: PersistentDataKey<T>, value: Any) {
                value as T
                profile.write(key, value)
            }

            val ecoProfile = PlayerProfile.load(player)

            for ((key, type) in keys) {
                val value = load(player.uniqueId).read(
                    key, when (type) {
                        Type.INT -> 0
                        Type.DOUBLE -> 0.0
                    }
                )

                val newKey = mappedKeys[key] ?: continue
                writeKey(ecoProfile, newKey, value)
            }
        }

        init {
            for (skill in Skills.values()) {
                keys[skill.id] = Type.INT
                mappedKeys[skill.id] = skill.dataKey
                keys[skill.id + "_progress"] = Type.DOUBLE
                mappedKeys[skill.id + "_progress"] = skill.dataXPKey
            }

            for (stat in Stats.values()) {
                keys[stat.id] = Type.INT
                mappedKeys[stat.id] = stat.dataKey
            }

            for (effect in Effects.values()) {
                keys[effect.id] = Type.INT
                mappedKeys[effect.id] = effect.dataKey
            }
        }
    }

    private enum class Type {
        DOUBLE,
        INT
    }
}