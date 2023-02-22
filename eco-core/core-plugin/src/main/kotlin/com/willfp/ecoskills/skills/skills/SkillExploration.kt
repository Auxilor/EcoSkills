package com.willfp.ecoskills.skills.skills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.*

class SkillExploration : Skill(
    "exploration"
) {
    private val fallDamageCache = Caffeine.newBuilder()
        .expireAfterWrite(this.config.getInt("fall-damage.limit-by-period.value").toLong(), TimeUnit.SECONDS)
        .build<UUID, Boolean>()

    private val fallDamageTimeoutEnabled = this.config.getBool("fall-damage.limit-by-period.enabled")

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: PlayerMoveEvent) {
        val player = event.player.filterSkillEnabled() ?: return

        if (!this.config.getBool("allow-flying") && player.isFlying) {
            return
        }

        val from = event.from
        val to = event.to ?: return

        if (from.world != to.world) {
            return
        }

        if (from.distance(to) > 100) {
            return
        }

        // If distance travelled on the Y axis is larger than distance travelled on X and Z, consider it a falling player, and dismiss
        if (abs(from.blockY - to.blockY) > sqrt((from.x - to.x).pow(2) + (from.z - to.z).pow(2))) {
            return
        }

        val speed = from.distance(to) / 0.2158
        var xp = this.config.getDouble("base-xp-to-give")
        if (this.config.getBool("multiply-xp-by-speed")) {
            xp *= speed
        }

        if (player.isFlying) {
            xp *= this.config.getDouble("flying-multiplier")
        }

        xp = round(xp)
        if (NumberUtils.randFloat(0.0, 100.0) < this.config.getDouble("xp-on-move-chance") && xp > 0.0) {
            player.giveSkillExperience(this, xp)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleLevelling(event: EntityDamageEvent) {
        val player = (event.entity as? Player)?.filterSkillEnabled() ?: return

        if (this.config.getStrings("disabled-in-worlds").containsIgnoreCase(player.getWorld().name)) {
            return
        }

        if (event.cause != EntityDamageEvent.DamageCause.FALL) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        if (fallDamageTimeoutEnabled && fallDamageCache.getIfPresent(player.uniqueId) == true) {
            return
        }

        if (event.finalDamage > player.health && !this.config.getBool("give-xp-on-fatal-fall")) {
            return
        }

        fallDamageCache.put(player.uniqueId, true)

        val xp = this.config.getDouble("fall-damage.xp-per-hp") * event.finalDamage
            player.giveSkillExperience(this, xp)
    }
}
