package com.willfp.ecoskills.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

/**
 * The player called [name], if they are online or have joined before.
 */
@Suppress("DEPRECATION")
internal fun knownPlayer(name: String): OfflinePlayer? =
    Bukkit.getPlayer(name) ?: Bukkit.getOfflinePlayer(name).takeIf { it.hasPlayedBefore() }
