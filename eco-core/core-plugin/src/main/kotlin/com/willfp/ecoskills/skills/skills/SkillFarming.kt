package com.willfp.ecoskills.skills.skills

import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.util.BlockUtils
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import java.util.*

class SkillFarming : Skill(
    "farming"
) {
    private val rewards: MutableMap<Material, Double>

    init {
        rewards = EnumMap(org.bukkit.Material::class.java)
    }

    override fun postUpdate() {
        rewards.clear()
        for (string in this.config.getStrings("xp-rewards", false)) {
            val split = string.split(":")
            val material = Material.getMaterial(split[0].uppercase()) ?: continue
            rewards[material] = split[1].toDouble()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handleLevelling(event: BlockBreakEvent) {
        if (this.config.getStrings("disabled-in-worlds").contains(event.block.world.name)) {
            return
        }

        val type = event.block.type
        val player = event.player

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
            return
        }

        if (event.block.blockData is Ageable && event.block.type != Material.SUGAR_CANE && event.block.type != Material.BAMBOO) {
            val data = event.block.blockData as Ageable
            if (data.age < data.maximumAge) {
                return
            }
        }

        if (BlockUtils.isPlayerPlaced(event.block) &&
            (event.block.type == Material.PUMPKIN || event.block.type == Material.MELON
                    || event.block.type == Material.SUGAR_CANE || event.block.type == Material.COCOA)
        ) {
            return
        }

        val toGive = rewards[type] ?: return

        player.giveSkillExperience(this, toGive)
    }
}