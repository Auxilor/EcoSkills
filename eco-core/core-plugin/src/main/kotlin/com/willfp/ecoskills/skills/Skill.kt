package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.config.SkillConfig
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.stats.Stats
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Skill(
    val id: String
) : Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey = plugin.namespacedKeyFactory.create(id)
    val xpKey: NamespacedKey = plugin.namespacedKeyFactory.create(id + "_progress")
    val config: Config
    lateinit var name: String
    lateinit var description: String
    lateinit var gui: SkillGUI
    var maxLevel: Int = 50
    private val rewards: MutableList<SkillObjectReward>
    private val guiLoreCache = HashMap<Int, List<String>>()
    private val messagesCache = HashMap<Int, List<String>>()

    init {
        config = SkillConfig(this.id, this.javaClass, plugin)
        rewards = ArrayList()

        Skills.registerNewSkill(this)
    }

    fun update() {
        name = config.getString("name")
        description = config.getString("description")
        maxLevel = config.getInt("max-level")
        rewards.clear()
        for (string in config.getStrings("rewards.rewards")) {
            val split = string.split("::")
            val asEffect = Effects.getByID(split[0].lowercase())
            val asStat = Stats.getByID(split[0].lowercase())
            if (asEffect != null) {
                rewards.add(SkillObjectReward(asEffect, SkillObjectOptions(split[1])))
            }
            if (asStat != null) {
                rewards.add(SkillObjectReward(asStat, SkillObjectOptions(split[1])))
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

        guiLoreCache.clear()
        messagesCache.clear()

        gui = SkillGUI(plugin, this)
    }

    fun getLevelUpRewards(): MutableList<SkillObjectReward> {
        return ArrayList(rewards)
    }

    fun getLevelUpReward(skillObject: SkillObject, to: Int): Int {
        for (reward in rewards) {
            if (reward.obj != skillObject) {
                continue
            }

            val opt = reward.options
            if (opt.startLevel > to || opt.endLevel < to) {
                continue
            }

            return reward.options.amountPerLevel
        }

        return 0
    }

    fun getCumulativeLevelUpReward(skillObject: SkillObject, to: Int): Int {
        var levels = 0
        for (i in 1..to) {
            levels += getLevelUpReward(skillObject, i)
        }

        return levels
    }

    fun getRewardsMessages(player: Player?, level: Int, useCache: Boolean = true): MutableList<String> {
        val messages = ArrayList<String>()
        if (messagesCache.containsKey(level) && useCache) {
            messages.addAll(messagesCache[level]!!)
        } else {
            var highestLevel = 1
            for (startLevel in this.config.getSubsection("rewards.chat-messages").getKeys(false)) {
                if (startLevel.toInt() > level) {
                    break
                }

                if (startLevel.toInt() > highestLevel) {
                    highestLevel = startLevel.toInt()
                }
            }

            for (string in this.config.getStrings("rewards.chat-messages.$highestLevel", false)) {
                var msg = string

                for (effect in Effects.values()) {
                    msg = msg.replace("%ecoskills_${effect.id}_description%", effect.getDescription(level))
                }
                messages.add(msg)
            }

            guiLoreCache[level] = messages
        }

        return StringUtils.formatList(messages, player)
    }

    fun getGUIRewardsMessages(player: Player?, level: Int, useCache: Boolean = true): MutableList<String> {
        val lore = ArrayList<String>()
        if (guiLoreCache.containsKey(level) && useCache) {
            lore.addAll(guiLoreCache[level]!!)
        } else {
            var highestLevel = 1
            for (startLevel in this.config.getSubsection("rewards.progression-lore").getKeys(false)) {
                if (startLevel.toInt() > level) {
                    break
                }

                if (startLevel.toInt() > highestLevel) {
                    highestLevel = startLevel.toInt()
                }
            }

            for (string in this.config.getStrings("rewards.progression-lore.$highestLevel", false)) {
                var s = string

                for (levelUpReward in this.getLevelUpRewards()) {
                    val skillObject = levelUpReward.obj
                    val objLevel = this.getCumulativeLevelUpReward(skillObject, level)

                    s = s.replace("%ecoskills_${skillObject.id}%", objLevel.toString())
                    s = s.replace("%ecoskills_${skillObject.id}_numeral%", NumberUtils.toNumeral(objLevel))

                    if (skillObject is Effect) {
                        s = s.replace("%ecoskills_${skillObject.id}_description%", skillObject.getDescription(objLevel))
                    }
                }

                lore.add(s)
            }

            guiLoreCache[level] = lore
        }

        return StringUtils.formatList(lore, player)
    }

    fun getGUILore(player: Player): MutableList<String> {
        val lore = ArrayList<String>()
        for (string in this.config.getStrings("gui.lore", false)) {
            lore.add(StringUtils.format(string, player))
        }
        return lore
    }

    open fun postUpdate() {
        // Override when needed
    }

    fun getExpForLevel(level: Int): Int {
        val req = this.plugin.configYml.getInts("skills.level-xp-requirements")
        return if (req.size <= level) {
            Int.MAX_VALUE
        } else {
            req[level - 1]
        }
    }
}