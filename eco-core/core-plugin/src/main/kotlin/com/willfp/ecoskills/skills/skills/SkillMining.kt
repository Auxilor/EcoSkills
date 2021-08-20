package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.getSkillProgress
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

class SkillMining : Skill(
    "mining"
) {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handleLevelling(event: BlockBreakEvent) {
        val type = event.block.type
        val player = event.player

        var xp = player.getSkillProgress(this)
        var toGive = 0.0
        for (string in this.config.getStrings("xp-rewards", false)) {
            if (string.startsWith(type.name.lowercase())) {
                toGive = string.split(":")[1].toDouble()
                break;
            }
        }

        if (toGive == 0.0) {
            return
        }

        val gainEvent = PlayerSkillExpGainEvent(player, this, toGive)
        Bukkit.getPluginManager().callEvent(gainEvent)
    }
}