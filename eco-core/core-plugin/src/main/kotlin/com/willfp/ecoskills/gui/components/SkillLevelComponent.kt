package com.willfp.ecoskills.gui.components

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.map.nestedMap
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpression
import com.willfp.eco.util.lineWrap
import com.willfp.ecomponent.components.LevelComponent
import com.willfp.ecomponent.components.LevelState
import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.util.LevelInjectable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

private val levelItemCache = Caffeine.newBuilder()
    .expireAfterWrite(plugin.configYml.getInt("gui.cache-ttl").toLong(), TimeUnit.MILLISECONDS)
    .build<Int, ItemStack>()

class SkillLevelComponent(
    private val plugin: EcoPlugin,
    private val skill: Skill
) : LevelComponent() {
    override val pattern: List<String> = plugin.configYml.getStrings("level-gui.progression-slots.pattern")
    override val maxLevel = skill.maxLevel

    private val itemCache = nestedMap<LevelState, Int, ItemStack>()

    override fun getLevelItem(player: Player, menu: Menu, level: Int, levelState: LevelState): ItemStack {
        val key = levelState.key

        fun item() = levelItemCache.get(player.hashCode() xor skill.hashCode() xor level.hashCode()) {
            ItemStackBuilder(Items.lookup(plugin.configYml.getString("level-gui.progression-slots.$key.item")))
                .setDisplayName(
                    plugin.configYml.getString("level-gui.progression-slots.$key.name")
                        .replace("%skill%", skill.name)
                        .let { skill.addPlaceholdersInto(it, level) }
                )
                .addLoreLines(
                    skill.addPlaceholdersInto(
                        plugin.configYml.getStrings("level-gui.progression-slots.$key.lore"),
                        player,
                        level = level
                    ).lineWrap(plugin.configYml.getInt("gui.skill-icon.line-wrap"))
                )
                .setAmount(
                    evaluateExpression(
                        plugin.configYml.getString("level-gui.progression-slots.item-amount"),
                        placeholderContext(
                            injectable = LevelInjectable(level)
                        )
                    ).roundToInt()
                )
                .build()
        }

        return if (levelState != LevelState.IN_PROGRESS) {
            itemCache[levelState].getOrPut(level) { item() }
        } else {
            item()
        }
    }

    override fun getLevelState(player: Player, level: Int): LevelState {
        return when {
            level <= player.getSkillLevel(skill) -> LevelState.UNLOCKED
            level == player.getSkillLevel(skill) + 1 -> LevelState.IN_PROGRESS
            else -> LevelState.LOCKED
        }
    }
}
