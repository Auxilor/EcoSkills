package com.willfp.ecoskills.skills.skills

import com.willfp.eco.util.BlockUtils
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

class SkillMining : Skill(
    "mining"
) {
    private val rewards: MutableMap<Material, Double>

    init {
        rewards = EnumMap(org.bukkit.Material::class.java)
    }

    override fun postUpdate() {
        rewards.clear()
        for (string in this.config.getStrings("xp-rewards")) {
            val split = string.split(":")
            val material = Material.getMaterial(split[0].uppercase()) ?: continue
            rewards[material] = split[1].toDouble()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handleLeveling(event: BlockBreakEvent) {
        val type = event.block.type
        val player = event.player.filterSkillEnabled() ?: return
        val toGive = rewards[type] ?: return

        if (BlockUtils.isPlayerPlaced(event.block)) {
            return
        }

        player.giveSkillExperience(this, toGive)
    }
}