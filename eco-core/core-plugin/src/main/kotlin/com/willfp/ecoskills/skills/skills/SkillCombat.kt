package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.GameMode
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
        if (this.config.getStrings("disabled-in-worlds").contains(event.killer.world.name)) {
            return
        }

        val player = event.killer.tryAsPlayer() ?: return

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

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