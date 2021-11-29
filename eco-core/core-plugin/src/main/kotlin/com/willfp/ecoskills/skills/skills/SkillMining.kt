package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.util.BlockUtils
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import java.util.EnumMap

class SkillMining : Skill(
    "mining"
) {
    private val rewards: MutableMap<Material, Double>

    companion object {
        val iarewards: MutableMap<String, Double> = mutableMapOf()
    }


    init {
        rewards = EnumMap(Material::class.java)
    }

    override fun postUpdate() {
        rewards.clear()
        iarewards.clear()
        for (string in this.config.getStrings("xp-rewards", false)) {
            val split = string.split(":")
            val material = Material.getMaterial(split[0].uppercase()) ?: continue
            rewards[material] = split[1].toDouble()
        }
        for (string in this.config.getStrings("xp-rewards-ia", false)) {
            val split = string.split(":")
            val material = split[0].replace(".", ":")
            iarewards[material] = split[1].toDouble()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handleLevelling(event: BlockBreakEvent) {
        val type = event.block.type
        val player = event.player

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val toGive = rewards[type] ?: return

        if (BlockUtils.isPlayerPlaced(event.block)) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        player.giveSkillExperience(this, toGive)
    }
}