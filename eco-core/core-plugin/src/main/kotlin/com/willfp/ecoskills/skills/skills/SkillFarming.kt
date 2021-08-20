package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.enchantments.Enchantment
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handleLevelling(event: BlockBreakEvent) {
        val type = event.block.type
        val player = event.player

        if (event.block.blockData !is Ageable) {
            return
        }

        val data = event.block.blockData as Ageable
        if (data.age < data.maximumAge) {
            return
        }

        val toGive = rewards[type] ?: return

        player.giveSkillExperience(this, toGive)
    }
}