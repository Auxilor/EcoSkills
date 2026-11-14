package com.willfp.ecoskills

import org.bukkit.OfflinePlayer
import wiki.ecohub.game.entity.runOwned

/**
 * Run [block] on the online player's region via [runOwned], or now if the player is offline
 * and so has no region.
 */
internal inline fun OfflinePlayer.runOwnedIfOnline(crossinline block: () -> Unit) {
    val online = this.player

    if (online == null) {
        block()
    } else {
        online.runOwned(block)
    }
}
