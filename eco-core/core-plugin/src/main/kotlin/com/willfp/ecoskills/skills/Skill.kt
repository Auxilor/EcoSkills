package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.*
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

abstract class Skill(
    val id: String
) : Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey
    val xpKey: NamespacedKey
    val uuid: UUID
    val config: Config
    lateinit var name: String
    lateinit var description: String
    lateinit var slot: Slot
    private val rewards: MutableMap<SkillObject, Int>

    init {
        key = plugin.namespacedKeyFactory.create(id)
        xpKey = plugin.namespacedKeyFactory.create(id + "_progress")
        uuid = UUID.nameUUIDFromBytes(id.toByteArray())
        config = plugin.configYml.getSubsection("skills.$id")
        rewards = HashMap()

        Skills.registerNewSkill(this)
    }

    fun update() {
        name = plugin.langYml.getString("skills.$id.name")
        description = plugin.langYml.getString("skills.$id.description")
        rewards.clear()
        for (string in config.getStrings("level-up-rewards")) {
            val split = string.split(":")
            val asEffect = Effects.getByID(split[0].lowercase())
            val asStat = Stats.getByID(split[0].lowercase())
            if (asEffect != null) {
                rewards[asEffect] = split[1].toInt()
            }
            if (asStat != null) {
                rewards[asStat] = split[1].toInt()
            }
        }

        PlaceholderEntry(
            id,
            { player -> player.getSkillLevel(this).toString() },
            true
        ).register()

        PlaceholderEntry(
            "${id}_numeral",
            { player -> NumberUtils.toNumeral(player.getSkillLevel(this)) },
            true
        ).register()

        postUpdate()

        slot = Slot.builder { player: Player ->
            ItemStackBuilder(
                Material.getMaterial(
                    config.getString("gui-item").uppercase()
                )!!
            ).setDisplayName(
                plugin.configYml.getString("gui.skill-icon.name")
                    .replace("%skill%", name)
                    .replace(
                        "%level%",
                        player.getSkillLevel(this).toString()
                    )
                    .replace(
                        "%level_numeral%",
                        NumberUtils.toNumeral(player.getSkillLevel(this))
                    )
            ).addLoreLines {
                val currentXP = player.getSkillProgress(this)
                val requiredXP = this.getExpForLevel(player.getSkillLevel(this) + 1)
                val lore: MutableList<String> = ArrayList()
                for (string in plugin.configYml.getStrings("gui.skill-icon.lore", false)) {
                    lore.add(
                        StringUtils.format(
                            string.replace("%description%", description)
                                .replace("%current_xp%", NumberUtils.format(currentXP))
                                .replace(
                                    "%required_xp%",
                                    NumberUtils.format(requiredXP.toDouble())
                                )
                                .replace(
                                    "%percentage_progress%",
                                    NumberUtils.format((currentXP / requiredXP) * 100) + "%"
                                ),
                            player
                        )
                    )
                }
                val skillSpecificIndex = lore.indexOf("%skill_specific%")
                if (skillSpecificIndex != -1) {
                    lore.removeAt(skillSpecificIndex)
                    lore.addAll(skillSpecificIndex, this.getGUILore(player))
                }
                lore
            }.build()
        }.build()
    }

    fun getLevelUpRewards(): Collection<SkillObject> {
        return rewards.keys
    }

    fun getLevelUpReward(skillObject: SkillObject): Int {
        return rewards[skillObject] ?: 0
    }

    fun getRewardsMessages(player: Player): MutableList<String> {
        val messages = ArrayList<String>()
        for (string in this.config.getStrings("rewards-messages", false)) {
            messages.add(StringUtils.format(string, player))
        }
        return messages
    }

    fun getGUILore(player: Player): MutableList<String> {
        val lore = ArrayList<String>()
        for (string in this.config.getStrings("gui-lore", false)) {
            lore.add(StringUtils.format(string, player))
        }
        return lore
    }

    open fun postUpdate() {
        // Override when needed
    }

    fun getExpForLevel(level: Int): Int {
        return this.plugin.configYml.getInts("skills.level-xp-requirements")[level - 1] ?: Integer.MAX_VALUE
    }
}