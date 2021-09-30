package com.willfp.ecoskills.data.storage

import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.stats.Stats
import org.bukkit.OfflinePlayer
import java.util.*

class PlayerProfile private constructor(
    private val data: MutableMap<String, Any>
) {
    fun <T : Any> write(key: String, value: T) {
        data[key] = value
    }

    fun readInt(key: String): Int {
        return data[key] as Int? ?: 0
    }

    fun readDouble(key: String): Double {
        return data[key] as Double? ?: 0.0
    }

    fun readString(key: String, default: String): String {
        return data[key] as String? ?: default
    }

    companion object {
        private val handler = EcoSkillsPlugin.getInstance().dataHandler
        private val loaded = mutableMapOf<UUID, PlayerProfile>()
        private val keys = mutableMapOf<String, Type>()

        private fun load(uuid: UUID): PlayerProfile {
            val found = loaded[uuid]
            if (found != null) {
                return found
            }

            val data = mutableMapOf<String, Any>()
            for ((key, type) in keys) {
                when (type) {
                    Type.INT -> data[key] = handler.readInt(uuid, key)
                    Type.DOUBLE -> data[key] = handler.readDouble(uuid, key)
                    Type.STRING -> data[key] = handler.readString(uuid, key)
                }
            }

            val profile = PlayerProfile(data)
            loaded[uuid] = profile
            return profile
        }

        fun saveAll(async: Boolean) {
            val saver = {
                for ((uuid, profile) in loaded) {
                    for ((key, type) in keys) {
                        when (type) {
                            Type.INT -> handler.write(uuid, key, profile.readInt(key))
                            Type.DOUBLE -> handler.write(uuid, key, profile.readDouble(key))
                            Type.STRING -> handler.write(uuid, key, profile.readString(key, "Unknown Value"))
                        }
                    }
                }

                handler.save()
            }

            if (async) {
                plugin.scheduler.runAsync(saver)
            } else {
                saver.invoke()
            }
        }

        val OfflinePlayer.profile: PlayerProfile
            get() {
                return load(this.uniqueId)
            }

        init {
            keys["name"] = Type.STRING

            for (skill in Skills.values()) {
                keys[skill.id] = Type.INT
                keys[skill.xpKey.key] = Type.DOUBLE
            }

            for (stat in Stats.values()) {
                keys[stat.id] = Type.INT
            }

            for (effect in Effects.values()) {
                keys[effect.id] = Type.INT
            }
        }
    }

    private enum class Type {
        STRING,
        DOUBLE,
        INT
    }
}