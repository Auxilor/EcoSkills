package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.CommandHandler
import com.willfp.eco.core.command.TabCompleteHandler
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.ecoskills.EcoSkillsPlugin
import com.willfp.ecoskills.getTotalSkillLevel
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.ceil

class CommandTop(plugin: EcoPlugin) :
    Subcommand(
        plugin,
        "top",
        "ecoskills.command.top",
        false
    ) {
    override fun getHandler(): CommandHandler {
        return CommandHandler { sender: CommandSender, args: List<String> ->
            var page = 1

            if (args.isNotEmpty()) {
                page = args[0].toIntOrNull() ?: 1
            }

            val plugin = plugin as EcoSkillsPlugin
            val uuidStrings = plugin.dataYml.getSubsectionOrNull("stats")?.getKeys(false) ?: ArrayList()
            val uuids = uuidStrings.stream().map { s -> UUID.fromString(s) }.toList()

            val top = TreeMap<OfflinePlayer, Int>()

            for (uuid in uuids) {
                val player = Bukkit.getOfflinePlayer(uuid)
                top[player] = player.getTotalSkillLevel()
            }

            val maxPage = ceil(top.size / 10.0).toInt()
            if (maxPage < page) {
                page = maxPage
            }

            val pagePlayers = HashMap<OfflinePlayer, Int>()

            val start = (page - 1) * 10
            val end = start + 10

            val players = top.descendingKeySet().toTypedArray()
            for (i in start..end) {
                val player = players[i] ?: continue
                pagePlayers[player] = top[player]!!
            }

            val messages = plugin.langYml.getStrings("top", false)
            val lines = ArrayList<String>()


            var rank = start
            for (entry in pagePlayers.entries) {
                val line = plugin.langYml.getString("top-line-format", false)
                    .replace("%rank%", rank.toString())
                    .replace("%player%", entry.key.name!!)
                    .replace("%level%", entry.value.toString())

                lines.add(line)

                rank++
            }

            val linesIndex = messages.indexOf("%lines%")
            if (linesIndex != -1) {
                messages.removeAt(linesIndex)
                messages.addAll(linesIndex, lines)
            }

            for (message in messages) {
                sender.sendMessage(StringUtils.format(message))
            }
        }
    }
}