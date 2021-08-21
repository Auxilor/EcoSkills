package com.willfp.ecoskills.api

import com.willfp.ecoskills.*
import com.willfp.ecoskills.api.modifier.ItemStatModifier
import com.willfp.ecoskills.api.modifier.StatModifier
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EcoSkillsAPIImpl: EcoSkillsAPI {
    override fun getSkillLevel(player: Player, skill: Skill): Int {
        return player.getSkillLevel(skill)
    }

    override fun giveSkillExperience(player: Player, skill: Skill, amount: Double) {
        player.giveSkillExperience(skill, amount)
    }

    override fun getSkillProgressToNextLevel(player: Player, skill: Skill): Double {
        return player.getSkillProgressToNextLevel(skill)
    }

    override fun getSkillProgressRequired(player: Player, skill: Skill): Int {
        return player.getSkillProgressRequired(skill)
    }

    override fun getSkillProgress(player: Player, skill: Skill): Double {
        return player.getSkillProgress(skill)
    }

    override fun getEffectLevel(player: Player, effect: Effect): Int {
        return player.getEffectLevel(effect)
    }

    override fun getStatLevel(player: Player, stat: Stat): Int {
        return player.getStatLevel(stat)
    }

    override fun addStatModifier(itemStack: ItemStack, modifier: ItemStatModifier) {
        itemStack.addStatModifier(modifier)
    }

    override fun removeStatModifier(itemStack: ItemStack, modifier: ItemStatModifier) {
        itemStack.removeStatModifier(modifier)
    }

    override fun removeStatModifier(player: Player, modifier: ItemStatModifier) {
        player.removeStatModifier(modifier)
    }

    override fun getStatModifierKeys(itemStack: ItemStack): MutableSet<NamespacedKey> {
        return itemStack.getStatModifierKeys()
    }

    override fun getStatModifierKeys(player: Player): MutableSet<NamespacedKey> {
        return player.getStatModifierKeys()
    }

    override fun getStatModifiers(itemStack: ItemStack): MutableSet<ItemStatModifier> {
        return itemStack.getStatModifiers()
    }

    override fun getStatModifiers(player: Player): MutableSet<StatModifier> {
        return player.getStatModifiers()
    }

    override fun getStatModifier(itemStack: ItemStack, key: NamespacedKey): ItemStatModifier? {
        return itemStack.getStatModifier(key)
    }

    override fun getStatModifier(player: Player, key: NamespacedKey): StatModifier? {
        return player.getStatModifier(key)
    }
}