package com.willfp.ecoskills.skills

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.*
import com.willfp.ecoskills.config.SkillConfig
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Skill(
    val id: String
) : Listener {
    protected val plugin: EcoPlugin = EcoSkillsPlugin.getInstance()

    val key: NamespacedKey = plugin.namespacedKeyFactory.create(id)
    val dataKey = PersistentDataKey(
        plugin.namespacedKeyFactory.create(id),
        PersistentDataKeyType.INT,
        0
    ).player()
    val dataXPKey = PersistentDataKey(
        plugin.namespacedKeyFactory.create("${id}_xp"),
        PersistentDataKeyType.DOUBLE,
        0.0
    ).player()
    val config: Config = SkillConfig(this.id, this.javaClass, plugin)
    lateinit var name: String
    lateinit var description: String
    lateinit var gui: SkillGUI
    var maxLevel: Int = 50
    private val rewards = mutableListOf<SkillObjectReward>()
    private val levelCommands = mutableMapOf<Int, MutableList<String>>()

    // Cached values
    private val guiLoreCache = mutableMapOf<Int, List<String>>()
    private val messagesCache = mutableMapOf<Int, List<String>>()

    init {
        finishLoading()
    }

    private fun finishLoading() {
        Skills.registerNewSkill(this)
    }

    fun update() {
        name = config.getFormattedString("name")
        description = config.getFormattedString("description")
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

        levelCommands.clear()
        for (string in config.getStrings("rewards.level-commands")) {
            val split = string.split(":")
            val level = split[0].toInt()
            val command = split[1]

            val commands = levelCommands[level] ?: mutableListOf()
            commands.add(command)
            levelCommands[level] = commands
        }

        PlayerPlaceholder(
            plugin,
            id
        ) { player -> player.getSkillLevel(this).toString() }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_percentage_progress"
        ) {
            val currentXP = it.getSkillProgress(this)
            val requiredXP = this.getExpForLevel(it.getSkillLevel(this) + 1)
            NumberUtils.format((currentXP / requiredXP) * 100)
        }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_current_xp"
        ) {
            NumberUtils.format(it.getSkillProgress(this))
        }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_required_xp"
        ) {
            this.getExpForLevel(it.getSkillLevel(this) + 1).toString()
        }.register()

        PlayerPlaceholder(
            plugin,
            "${id}_numeral"
        ) { player -> NumberUtils.toNumeral(player.getSkillLevel(this)) }.register()

        PlayerlessPlaceholder(
            plugin,
            "${id}_name"
        ) { this.name }.register()

        PlayerPlaceholder(
            plugin,
            "average_skill_level"
        ) { player -> NumberUtils.format(player.getAverageSkillLevel()) }.register()

        PlayerPlaceholder(
            plugin,
            "total_skill_level"
        ) { player -> player.getTotalSkillLevel().toString() }.register()

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
        val messages = mutableListOf<String>()
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

            for (string in this.config.getStrings("rewards.chat-messages.$highestLevel")) {
                var msg = string

                for (levelUpReward in this.getLevelUpRewards()) {
                    val skillObject = levelUpReward.obj

                    if (skillObject is Effect) {
                        val objLevel = this.getCumulativeLevelUpReward(skillObject, level)
                        msg = msg.replace(
                            "%ecoskills_${skillObject.id}_description%",
                            skillObject.getDescription(objLevel)
                        )
                    }
                }
                messages.add(msg)
            }

            messagesCache[level] = messages
        }

        return StringUtils.formatList(messages, player)
    }

    fun getGUIRewardsMessages(player: Player?, level: Int, useCache: Boolean = true): MutableList<String> {
        val lore = mutableListOf<String>()
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

            for (string in this.config.getStrings("rewards.progression-lore.$highestLevel")) {
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
        val lore = mutableListOf<String>()
        for (string in this.config.getStrings("gui.lore")) {
            lore.add(StringUtils.format(string, player))
        }
        return lore
    }

    fun executeLevelCommands(player: Player, level: Int) {
        val commands = levelCommands[level] ?: emptyList()

        for (command in commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.name))
        }
    }

    open fun postUpdate() {
        // Override when needed
    }

    fun getExpForLevel(level: Int): Int {
        val req = this.config.getInts("level-xp-requirements")
        return if (req.size < level) {
            Int.MAX_VALUE
        } else {
            req[level - 1]
        }
    }
}