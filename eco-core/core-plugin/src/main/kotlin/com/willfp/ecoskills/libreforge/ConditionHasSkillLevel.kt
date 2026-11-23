package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerSkillLevelUpEvent
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.skills.Skills
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import wiki.ecohub.config.ArgType
import wiki.ecohub.config.Config
import wiki.ecohub.config.arguments
import wiki.ecohub.core.registry.NoCompileData
import wiki.ecohub.game.entity.toDispatcher
import wiki.ecohub.rules.condition.Condition
import wiki.ecohub.rules.holder.Dispatcher
import wiki.ecohub.rules.holder.ProvidedHolder
import wiki.ecohub.rules.holder.get
import wiki.ecohub.rules.holder.updateEffects

object ConditionHasSkillLevel : Condition<NoCompileData>("has_skill_level") {
    override val description = "Passes when the player's level in the given skill is at or above the minimum."

    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "skill",
            "You must specify the skill!",
            description = "The skill to check the level of.",
            type = ArgType.STRING
        )
        require(
            "level",
            "You must specify the skill level!",
            description = "The minimum skill level required.",
            type = ArgType.EXPRESSION,
            example = "5 + %level%"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val skill = Skills.getByID(config.getString("skill").lowercase()) ?: return false

        return player.getSkillLevel(skill) >= config.getIntFromExpression("level", player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PlayerSkillLevelUpEvent) {
        event.player.toDispatcher().updateEffects()
    }
}
