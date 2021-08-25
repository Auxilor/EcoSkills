package com.willfp.ecoskills.skills.skills

import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent

class SkillExploration : Skill(
    "exploration"
) {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: PlayerMoveEvent) {
        val player = event.player

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val from = event.from
        val to = event.to ?: return

        if (from.world != to.world) {
            return
        }

        val speed = from.distance(to) / 0.2158
        var xp = this.config.getDouble("base-xp-to-give")
        if (this.config.getBool("multiply-xp-by-speed")) {
            xp *= speed
        }

        if (NumberUtils.randFloat(0.0, 100.0) < this.config.getDouble("xp-on-move-chance")) {
            player.giveSkillExperience(this, xp)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handleLevelling(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }

        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        val player = event.entity as Player

        val xp = this.config.getDouble("fall-damage-xp-per-hp") * event.finalDamage
        player.giveSkillExperience(this, xp)
    }
}