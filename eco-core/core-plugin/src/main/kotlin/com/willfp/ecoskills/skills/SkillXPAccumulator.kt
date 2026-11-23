package com.willfp.ecoskills.skills

import com.willfp.ecoskills.api.gainSkillXP
import com.willfp.ecoskills.plugin
import org.bukkit.GameMode
import org.bukkit.entity.Player
import wiki.ecohub.core.platform.NumericalPermissions
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.provider.protection.ExternalAFKService
import wiki.ecohub.rules.counter.Accumulator
import wiki.ecohub.rules.holder.EmptyProvidedHolder
import java.time.Duration

class SkillXPAccumulator(
    private val skill: Skill
) : Accumulator {
    override fun accept(player: Player, count: Double) {
        if (player.isInDisabledWorld) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && ExternalAFKService.isAfk(player)) {
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


private val xpMultiplierCache = EcoCache.builder<Player, Double>().expireAfterWrite(Duration.ofSeconds(10)).build {
    it.cacheSkillXPMultiplier()
}

val Player.skillXPMultiplier: Double
    get() = xpMultiplierCache.get(this) { it.cacheSkillXPMultiplier() }

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
    // Delegates to eco so the four copies of this loop cannot drift apart again. Two
    // behaviour fixes come with it: a permission explicitly set to false no longer counts,
    // and a negative value is honoured rather than lost to a `Double.MIN_VALUE` seed - which
    // is the smallest *positive* double, so `.-50` used to resolve to roughly zero.
    //
    // The node itself stays this plugin's own; eco supplies the arithmetic, never a prefix.
    return NumericalPermissions.highest(
        this.effectivePermissions.filter { it.value }.map { it.permission },
        permission,
        default
    )
}
