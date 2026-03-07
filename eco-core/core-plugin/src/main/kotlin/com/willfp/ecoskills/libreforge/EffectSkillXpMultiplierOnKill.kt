package com.willfp.ecoskills.libreforge

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.ecoskills.api.event.PlayerSkillXPGainEvent
import com.willfp.ecoskills.skills.Skills
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import java.util.UUID
import java.util.WeakHashMap

object EffectSkillXpMultiplierOnKill : Effect<NoCompileData>("skill_xp_multiplier_on_kill") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val recentKills = WeakHashMap<UUID, KillData>()
    
    private val ecoPlugin: EcoPlugin
        get() = EcoPlugin.getPlugin(EcoPlugin::class.java)

    data class KillData(
        val entity: LivingEntity,
        val multiplier: Double,
        val skills: List<String>?
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val victim = data.victim as? LivingEntity ?: return false
        
        val multiplier = config.getDoubleFromExpression("multiplier", data)
        val skills = if (config.has("skills")) {
            config.getStrings("skills")
        } else {
            null
        }
        
        recentKills[player.uniqueId] = KillData(victim, multiplier, skills)
        
        Bukkit.getScheduler().runTaskLater(ecoPlugin, Runnable {
            recentKills.remove(player.uniqueId)
        }, 40L)
        
        return true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: PlayerSkillXPGainEvent) {
        val player = event.player
        val killData = recentKills[player.uniqueId] ?: return

        if (killData.skills != null && event.skill.id !in killData.skills) return

        event.gainedXP *= killData.multiplier
    }
}
