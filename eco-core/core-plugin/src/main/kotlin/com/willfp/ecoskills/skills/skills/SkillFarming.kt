package com.willfp.ecoskills.skills.skills

import com.willfp.eco.util.BlockUtils
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import java.util.EnumMap

class SkillFarming : Skill(
    "farming"
) {
    private val rewards: MutableMap<Material, Double>
    private val stackedCrops = listOf(
        Material.SUGAR_CANE,
        Material.BAMBOO
    )

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

        val toGive = rewards[type] ?: return

        var amount = 1

        if (type in stackedCrops) {
            var current = event.block.getRelative(BlockFace.UP)
            while (current.type == type) {
                amount++
                current = current.getRelative(BlockFace.UP)
            }
        }

        player.giveSkillExperience(this, toGive*amount)
    }
}
