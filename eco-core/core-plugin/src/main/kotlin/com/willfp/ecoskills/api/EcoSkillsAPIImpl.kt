package com.willfp.ecoskills.api

import com.willfp.ecoskills.addStatModifier
import com.willfp.ecoskills.api.modifier.ItemStatModifier
import com.willfp.ecoskills.api.modifier.PlayerStatModifier
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.getAverageSkillLevel
import com.willfp.ecoskills.getBaseStatLevel
import com.willfp.ecoskills.getBonusStatLevel
import com.willfp.ecoskills.getEffectLevel
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getSkillProgress
import com.willfp.ecoskills.getSkillProgressRequired
import com.willfp.ecoskills.getSkillProgressToNextLevel
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.getStatModifier
import com.willfp.ecoskills.getStatModifierKeys
import com.willfp.ecoskills.getStatModifiers
import com.willfp.ecoskills.getTotalSkillLevel
import com.willfp.ecoskills.giveSkillExperience
import com.willfp.ecoskills.isSkillEnabled
import com.willfp.ecoskills.removeStatModifier
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.stats.Stat
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EcoSkillsAPIImpl : EcoSkillsAPI {
    override fun getSkillLevel(player: OfflinePlayer, skill: Skill): Int {
        return player.getSkillLevel(skill)
    }

    override fun giveSkillExperience(player: Player, skill: Skill, amount: Double) {
        player.giveSkillExperience(skill, amount)
    }

    override fun giveSkillExperience(player: Player, skill: Skill, amount: Double, applyMultipliers: Boolean) {
        player.giveSkillExperience(skill, amount, noMultiply = !applyMultipliers)
    }

    override fun getSkillProgressToNextLevel(player: OfflinePlayer, skill: Skill): Double {
        return player.getSkillProgressToNextLevel(skill)
    }

    override fun getSkillProgressRequired(player: OfflinePlayer, skill: Skill): Int {
        return player.getSkillProgressRequired(skill)
    }

    override fun getSkillProgress(player: OfflinePlayer, skill: Skill): Double {
        return player.getSkillProgress(skill)
    }

    override fun getEffectLevel(player: OfflinePlayer, effect: Effect): Int {
        return player.getEffectLevel(effect)
    }

    override fun getSkillEnabled(player: OfflinePlayer, skill: Skill): Boolean {
        return player.isSkillEnabled(skill)
    }

    override fun getStatLevel(player: OfflinePlayer, stat: Stat): Int {
        return player.getStatLevel(stat)
    }

    override fun getBaseStatLevel(player: OfflinePlayer, stat: Stat): Int {
        return player.getBaseStatLevel(stat)
    }

    override fun getBonusStatLevel(player: Player, stat: Stat): Int {
        return player.getBonusStatLevel(stat)
    }

    override fun addStatModifier(itemStack: ItemStack, modifier: ItemStatModifier) {
        itemStack.addStatModifier(modifier)
    }

    override fun removeStatModifier(itemStack: ItemStack, modifier: ItemStatModifier) {
        itemStack.removeStatModifier(modifier.key)
    }

    override fun removeStatModifier(itemStack: ItemStack, key: NamespacedKey) {
        itemStack.removeStatModifier(key)
    }

    override fun addStatModifier(player: Player, modifier: PlayerStatModifier) {
        player.addStatModifier(modifier)
    }

    override fun removeStatModifier(player: Player, modifier: PlayerStatModifier) {
        player.removeStatModifier(modifier.key)
    }

    override fun removeStatModifier(player: Player, key: NamespacedKey) {
        player.removeStatModifier(key)
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

    override fun getStatModifiers(player: Player): MutableSet<PlayerStatModifier> {
        return player.getStatModifiers()
    }

    override fun getStatModifier(itemStack: ItemStack, key: NamespacedKey): ItemStatModifier? {
        return itemStack.getStatModifier(key)
    }

    override fun getStatModifier(player: Player, key: NamespacedKey): PlayerStatModifier? {
        return player.getStatModifier(key)
    }

    override fun getAverageSkillLevel(player: OfflinePlayer): Double {
        return player.getAverageSkillLevel()
    }

    override fun getTotalSkillLevel(player: OfflinePlayer): Int {
        return player.getTotalSkillLevel()
    }
}