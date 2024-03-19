package com.willfp.ecoskills.proxy.v1_20_R3

import com.willfp.eco.core.packet.PacketEvent
import com.willfp.ecoskills.actionbar.ActionBarCompatibilityProxy
import com.willfp.ecoskills.actionbar.pausePersistentActionBar
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

class ActionBarCompatibility : ActionBarCompatibilityProxy {
    private val ClientboundSystemChatPacket.isActionBar: Boolean
        get() = this::class.java.declaredFields
            .first { it.type == Boolean::class.java }
            .apply { isAccessible = true }
            .get(this) as Boolean

    override fun onSend(event: PacketEvent) {
        val player = event.player

        when (val packet = event.packet.handle) {
            is ClientboundSetActionBarTextPacket -> player.pausePersistentActionBar()
            is ClientboundSystemChatPacket -> if (packet.isActionBar) player.pausePersistentActionBar()
        }
    }
}
