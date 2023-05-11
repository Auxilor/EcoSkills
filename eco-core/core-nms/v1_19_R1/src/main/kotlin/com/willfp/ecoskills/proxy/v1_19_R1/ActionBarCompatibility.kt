package com.willfp.ecoskills.proxy.v1_19_R1

import com.willfp.eco.core.packet.PacketEvent
import com.willfp.ecoskills.actionbar.ActionBarCompatibilityProxy
import com.willfp.ecoskills.actionbar.pausePersistentActionBar
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket

class ActionBarCompatibility : ActionBarCompatibilityProxy {
    override fun onSend(event: PacketEvent) {
        val player = event.player

        when (event.packet.handle) {
            is ClientboundSetActionBarTextPacket -> player.pausePersistentActionBar()
        }
    }
}
