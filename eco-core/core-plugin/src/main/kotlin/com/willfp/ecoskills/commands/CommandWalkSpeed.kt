package com.willfp.ecoskills.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stats
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.floor

class CommandWalkSpeed(plugin: EcoPlugin) :
    PluginCommand(
        plugin,
        "walkspeed",
        "ecoskills.command.walkspeed",
        false
    ) {

    var globalMaxSpeed = 2.0F

    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (sender !is Player) {
            sender.sendMessage(this.plugin.langYml.getMessage("not-player"))
            return
        }

        val maxSpeed = computeMaxSpeed(sender)

        if (args.isEmpty()) {
            sender.walkSpeed = (1.0 / 5.0).toFloat()
            sender.sendMessage("§aTvá rychlost chůze byla nastavena na §e100%")
            return
        }

        val speed = args[0].toFloatOrNull()

        if (speed == null || speed < 1 || speed > maxSpeed) {
            sender.sendMessage("§6Rychlost musí být číslo od 1.0 do $maxSpeed")
            return
        }

        sender.walkSpeed = (speed / 5.0).toFloat()
        sender.sendMessage("§aTvá rychlost chůze byla nastavena na §e${(speed * 100).toInt()}%")
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1 && sender is Player) {
            val maxSpeed = computeMaxSpeed(sender)

            for (i in 10..floor(maxSpeed * 10).toInt()) {
                completions.add(String.format("%.1f", i / 10F))
            }
            return completions
        }

        return emptyList()
    }

    private fun computeMaxSpeed(player: Player): Float {
        val speedStat = Stats.SPEED

        var permissionMaxSpeed: Float? = null
        val prefix = "ecoskills.command.walkspeed.max."

        if(player.isOp)
            return 5.0F

        for (permissionAttachmentInfo in player.effectivePermissions) {
            val permission = permissionAttachmentInfo.permission
            if (permission.startsWith(prefix)) {
                (permission.substring(permission.lastIndexOf(".") + 1).toIntOrNull())?.let {
                    if (it >= 100 && it <= globalMaxSpeed * 100)
                        permissionMaxSpeed = it.toFloat() / 100
                    else
                        permissionMaxSpeed = globalMaxSpeed
                }
            }
        }

        return permissionMaxSpeed ?: (1 + (speedStat.config.getDouble("percent-faster-per-level") * player.getStatLevel(
            speedStat
        )) / 100.0).toFloat()
    }
}
