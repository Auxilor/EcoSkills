package com.willfp.ecoskills.libreforge

import com.willfp.ecoskills.api.event.PlayerRegenMagicEvent
import com.willfp.ecoskills.magic.MagicType
import com.willfp.ecoskills.magic.MagicTypes
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler
import kotlin.math.roundToInt

object EffectMagicRegenMultiplier : MultiMultiplierEffect<MagicType>("magic_regen_multiplier") {
    override val key = "types"

    override fun getElement(key: String): MagicType? {
        return MagicTypes.getByID(key)
    }

    override fun getAllElements(): Collection<MagicType> {
        return MagicTypes.values()
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerRegenMagicEvent) {
        val player = event.player

        event.amount *= getMultiplier(player.toDispatcher(), event.magicType).roundToInt()
    }
}
