package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.metadata.FixedMetadataValue

class SkillCombat : Skill(
    "combat"
) {
    @EventHandler(priority = EventPriority.MONITOR)
    fun handleLevelling(event: EntityDeathByEntityEvent) {
        val player = event.killer.tryAsPlayer().filterSkillEnabled() ?: return

        if (this.config.getBool("prevent-levelling-from-spawners") && event.victim.hasMetadata("from-spawner")) {
            return
        }

        val xp = event.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value * this.config.getDouble("xp-per-heart")
        player.giveSkillExperience(this, xp)
    }

    @EventHandler
    fun onSpawn(event: CreatureSpawnEvent) {
        if (event.spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.entity.setMetadata("from-spawner", FixedMetadataValue(this.plugin, true))
        }
    }
}