package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getSkillProgress
import com.willfp.ecoskills.setSkillLevel
import com.willfp.ecoskills.setSkillProgress
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class SkillLevellingListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onProgress(event: PlayerSkillExpGainEvent) {
        val player = event.player
        val skill = event.skill
        val amount = event.amount
        val level = player.getSkillLevel(skill)

        player.setSkillProgress(skill, player.getSkillProgress(skill) + amount)

        if (player.getSkillProgress(skill) >= skill.getExpForLevel(level + 1)) {
            player.setSkillProgress(skill, 0.0)
            player.setSkillLevel(skill, level + 1)
            val levelUpEvent = PlayerSkillLevelUpEvent(player, skill, level + 1)
            Bukkit.getPluginManager().callEvent(levelUpEvent)
        }
    }
}