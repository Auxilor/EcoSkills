package com.willfp.ecoskills.skills.skills

import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.skills.Skill
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
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

        val toGive = rewards[type] ?: return

        if (player.inventory.itemInMainHand.getEnchantmentLevel(Enchantment.SILK_TOUCH) != 0) {
            return
        }

        val gainEvent = PlayerSkillExpGainEvent(player, this, toGive)
        Bukkit.getPluginManager().callEvent(gainEvent)
    }
}