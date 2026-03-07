package com.willfp.ecoskills.skills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.plugin
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.counters.Accumulator
import com.willfp.libreforge.toDispatcher
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit
import kotlin.math.max

class SkillXPAccumulator(
    private val skill: Skill
) : Accumulator {
    override fun accept(player: Player, count: Double) {
        if (player.isInDisabledWorld) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        if (player.gameMode in setOf(GameMode.CREATIVE, GameMode.SPECTATOR)) {
            return
        }

        if (!skill.conditions.areMet(player.toDispatcher(), EmptyProvidedHolder)) {
            return
        }

        player.gainSkillXP(skill, count)
    }
}


private val xpMultiplierCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build<Player, Double> {
    it.cacheSkillXPMultiplier()
}

val Player.skillXPMultiplier: Double
    get() = xpMultiplierCache.get(this)

private fun Player.cacheSkillXPMultiplier(): Double {
    if (this.hasPermission("ecoskills.xpmultiplier.quadruple")) {
        return 4.0
    }

    if (this.hasPermission("ecoskills.xpmultiplier.triple")) {
        return 3.0
    }

    if (this.hasPermission("ecoskills.xpmultiplier.double")) {
        return 2.0
    }

    if (this.hasPermission("ecoskills.xpmultiplier.50percent")) {
        return 1.5
    }

    return 1 + getNumericalPermission("ecoskills.xpmultiplier", 0.0) / 100
}

fun Player.getNumericalPermission(permission: String, default: Double): Double {
    var highest: Double? = null

    for (permissionAttachmentInfo in this.effectivePermissions) {
        val perm = permissionAttachmentInfo.permission
        if (perm.startsWith(permission)) {
            val found = perm.substring(perm.lastIndexOf(".") + 1).toDoubleOrNull() ?: continue
            highest = max(highest ?: Double.MIN_VALUE, found)
        }
    }

    return highest ?: default
}
