package com.willfp.ecoskills.stats.stats

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class StatCritDamage : Stat(
    "crit_damage"
) {
}