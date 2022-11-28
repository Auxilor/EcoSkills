package com.willfp.ecoskills.skills.skills

import com.willfp.eco.util.BlockUtils
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import java.util.EnumMap

class SkillFarming : Skill(
    "farming"
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
    fun handleLevelling(event: BlockBreakEvent) {
        val player = event.player.filterSkillEnabled() ?: return

        val type = event.block.type

        val toIgnoreAge = listOf(
            Material.SUGAR_CANE,
            Material.BAMBOO,
            Material.GLOW_BERRIES,
            Material.SWEET_BERRY_BUSH
        )

        if (event.block.blockData is Ageable && !toIgnoreAge.contains(event.block.type)) {
            val data = event.block.blockData as Ageable
            if (data.age < data.maximumAge) {
                return
            }
        }

        val toCheckPlayers = listOf(
            Material.PUMPKIN,
            Material.MELON,
            Material.SUGAR_CANE
        )

        if (BlockUtils.isPlayerPlaced(event.block) && toCheckPlayers.contains(event.block.type)) {
            return
        }

        if (type == Material.SUGAR_CANE) {
            val block = event.block
            val blockAbove = block.getRelative(0, 1, 0)
            val blockAbove2 = block.getRelative(0, 2, 0)
            val toGive = rewards[type] ?: return

            if (blockAbove.type == Material.SUGAR_CANE && !BlockUtils.isPlayerPlaced(blockAbove)) {
                player.giveSkillExperience(this, toGive)
            }
            if (blockAbove2.type == Material.SUGAR_CANE && !BlockUtils.isPlayerPlaced(blockAbove2)) {
                player.giveSkillExperience(this, toGive)
            }
        }

        val toGive = rewards[type] ?: return

        player.giveSkillExperience(this, toGive)
    }
}
