package com.willfp.ecoskills.gui.components

import com.willfp.ecoskills.api.getSkillLevel
import com.willfp.ecoskills.plugin
import com.willfp.ecoskills.skills.Skill
import com.willfp.ecoskills.util.LevelInjectable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import wiki.ecohub.data.cache.EcoCache
import wiki.ecohub.game.item.Items
import wiki.ecohub.game.item.builder.ItemStackBuilder
import wiki.ecohub.math.evaluateExpression
import wiki.ecohub.modules.menus.component.LevelComponent
import wiki.ecohub.modules.menus.component.LevelState
import wiki.ecohub.modules.menus.menu.Menu
import wiki.ecohub.ui.placeholder.placeholderContext
import wiki.ecohub.ui.text.lineWrap
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

private data class SkillLevelKey(val player: UUID, val skill: String, val level: Int)

private val levelItemCache = EcoCache.builder<SkillLevelKey, ItemStack>()
    .expireAfterWrite(Duration.ofMillis(plugin.configYml.getInt("gui.cache-ttl").toLong()))
    .build()

class SkillLevelComponent(
    private val skill: Skill
) : LevelComponent() {
    override val pattern: List<String> = plugin.configYml.getStrings("level-gui.progression-slots.pattern")
    override val maxLevel = skill.maxLevel

    private val itemCache = ConcurrentHashMap<LevelState, MutableMap<Int, ItemStack>>()

    override fun getLevelItem(player: Player, menu: Menu, level: Int, levelState: LevelState): ItemStack {
        val key = levelState.key

        fun item() = levelItemCache.get(SkillLevelKey(player.uniqueId, skill.id, level)) {
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
            itemCache.computeIfAbsent(levelState) { ConcurrentHashMap() }.computeIfAbsent(level) { item() }
        } else {
            item()
        }
    }

    override fun getLevelState(player: Player, level: Int): LevelState =
        LevelState.of(level, player.getSkillLevel(skill))
}
