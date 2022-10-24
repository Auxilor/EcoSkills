package com.willfp.ecoskills.skills

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.core.placeholder.PlayerlessPlaceholder
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.SkillObject
import com.willfp.ecoskills.config.SkillConfig
import com.willfp.ecoskills.effects.Effect
import com.willfp.ecoskills.effects.Effects
import com.willfp.ecoskills.getAverageSkillLevel
import com.willfp.ecoskills.getSkillLevel
import com.willfp.ecoskills.getSkillProgress
import com.willfp.ecoskills.getTotalSkillLevel
import com.willfp.ecoskills.stats.Stats
import org.bukkit.Bukkit
import org.bukkit.GameMode
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
    )
    val dataXPKey = PersistentDataKey(
        plugin.namespacedKeyFactory.create("${id}_xp"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )
    val config: Config = SkillConfig(this.id, this.javaClass, plugin)
    val xpRequirements = config.getInts("level-xp-requirements").toMutableList()
    lateinit var name: String
    lateinit var levelName: String
    lateinit var description: String
    lateinit var gui: SkillLevelGUI
    var maxLevel: Int = 50
    private val rewards = mutableListOf<SkillObjectReward>()
    private val levelCommands = mutableMapOf<Int, MutableList<String>>()
    var enabled = config.getBoolOrNull("enabled") ?: true

    // Cached values
    private val guiLoreCache = Caffeine.newBuilder()
        .build<Int, List<String>>()
    private val messagesCache = Caffeine.newBuilder()
        .build<Int, List<String>>()

    init {
        finishLoading()
    }

    private fun finishLoading() {
        Skills.registerNewSkill(this)
    }

    protected fun Player?.filterSkillEnabled(): Player? {
        val player = this ?: return null
        with(this@Skill) {
            if (!this.enabled) {
                return null
            }

            if (this.config.getStrings("disabled-in-worlds").containsIgnoreCase(player.world.name)) {
                return null
            }

            if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
                return null
            }

            if (plugin.configYml.getBool("skills.prevent-levelling-while-afk") && AFKManager.isAfk(player)) {
                return null
            }

            return player
        }
    }

    fun update() {
        enabled = config.getBoolOrNull("enabled") ?: true
        name = config.getFormattedString("name")
        levelName = config.getFormattedStringOrNull("gui.name") ?: name
        description = config.getFormattedString("description")

        xpRequirements.clear()
        xpRequirements.addAll(config.getInts("level-xp-requirements"))

        maxLevel = xpRequirements.size - 1

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

            if (split.size == 1) {
                for (level in 1..maxLevel) {
                    val commands = levelCommands[level] ?: mutableListOf()
                    commands.add(string)
                    levelCommands[level] = commands
                }
            } else {
                val level = split[0].toInt()

                val command = string.removePrefix("$level:")
                val commands = levelCommands[level] ?: mutableListOf()
                commands.add(command)
                levelCommands[level] = commands
            }
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

        guiLoreCache.invalidateAll()
        messagesCache.invalidateAll()

        gui = SkillLevelGUI(plugin, this)
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

    fun getRewardsMessages(player: Player?, level: Int): List<String> {
        val raw = messagesCache.get(level) {
            val parentSection = this.config.getSubsection("rewards.chat-messages")

            var highestConfiguredLevel = 1
            for (messagesLevel in parentSection.getKeys(false).map { it.toInt() }) {
                if (messagesLevel > level) {
                    continue
                }

                if (messagesLevel > highestConfiguredLevel) {
                    highestConfiguredLevel = messagesLevel
                }
            }

            val messages = mutableListOf<String>()

            for (string in this.config.getStrings("rewards.chat-messages.$highestConfiguredLevel")) {
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

            messages
        }

        return StringUtils.formatList(raw, player)
    }

    fun getGUIRewardsMessages(player: Player?, level: Int): MutableList<String> {
        val raw = guiLoreCache.get(level) {
            val parentSection = this.config.getSubsection("rewards.progression-lore")

            var highestConfiguredLevel = 1
            for (messagesLevel in parentSection.getKeys(false).map { it.toInt() }) {
                if (messagesLevel > level) {
                    continue
                }

                if (messagesLevel > highestConfiguredLevel) {
                    highestConfiguredLevel = messagesLevel
                }
            }

            val messages = mutableListOf<String>()

            for (string in this.config.getStrings("rewards.progression-lore.$highestConfiguredLevel")) {
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

                messages.add(s)
            }

            messages
        }

        return StringUtils.formatList(raw, player)
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
        val req = xpRequirements
        return if (maxLevel < level) {
            Int.MAX_VALUE
        } else {
            req[level - 1]
        }
    }
}