package com.willfp.ecoskills.integrations

import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.util.BlockUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skills
import com.willfp.ecoskills.skills.skills.SkillMining
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ItemsAdderMiningLeveller(
    private val plugin: EcoSkillsPlugin
) : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handleLevelling(event: CustomBlockBreakEvent){
        val type = event.namespacedID
        val player = event.player

        if(player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR){
            return
        }

        val toGive = SkillMining.iarewards[type] ?: return

        if(BlockUtils.isPlayerPlaced(event.block)){
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        player.giveSkillExperience(Skills.MINING, toGive)
    }
}