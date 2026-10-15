package com.willfp.ecoskills.util

import com.willfp.eco.core.command.CommandBase
import com.willfp.eco.util.toSingletonList
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

fun CommandBase.players(args: List<String>, position: Int, key: String): List<Player> {
    val arg = this.notifyNull(args.getOrNull(position), key)

    return if (arg.equals("all", ignoreCase = true)) {
        Bukkit.getOnlinePlayers().toList()
    } else {
        Bukkit.getPlayer(arg)?.toSingletonList() ?: emptyList()
    }
}

fun CommandBase.offlinePlayer(args: List<String>, position: Int, key: String): OfflinePlayer? {
    val arg = this.notifyNull(args.getOrNull(position), key)

    return Bukkit.getPlayer(arg) ?: Bukkit.getOfflinePlayer(arg).takeIf { it.hasPlayedBefore() }
}
