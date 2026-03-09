package com.willfp.ecoskills.skills.display

import com.willfp.eco.util.namespacedKeyOf
import com.willfp.ecoskills.plugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player

// Maps BossBar's to their expiry time
private val bossBars = mutableMapOf<NamespacedKey, Long>()

fun Player.sendTemporaryBossBar(
    message: String,
    identifier: String, // The identifier for the boss bar (e.g. The skill name)
    duration: Int, // In milliseconds
    barColor: BarColor,
    barStyle: BarStyle,
    progress: Double // Progress between 0 and 1
) {
    val idBytes = (this.uniqueId.hashCode() shl 5) xor identifier.hashCode()

    val key = namespacedKeyOf("ecoskills", idBytes.toString())

    val bossBar = Bukkit.getBossBar(key) ?: Bukkit.createBossBar(
        key,
        message,
        barColor,
        barStyle
    )

    bossBar.setTitle(message)
    bossBar.progress = progress
    bossBar.addPlayer(this)

    bossBars[key] = System.currentTimeMillis() + duration
}

object TemporaryBossBarHandler {
    internal fun startTicking() {
        plugin.scheduler.runTimer(5, 5) {
            val iterator = bossBars.iterator()

            while (iterator.hasNext()) {
                val (key, expiry) = iterator.next()

                if (System.currentTimeMillis() > expiry) {
                    val bossBar = Bukkit.getBossBar(key) ?: continue
                    bossBar.removeAll()
                    Bukkit.removeBossBar(key)
                    iterator.remove()
                }
            }
        }
    }
}
