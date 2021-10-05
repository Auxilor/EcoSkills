package com.willfp.ecoskills.attackspeed

import com.willfp.ecoskills.getStatLevel
import com.willfp.ecoskills.stats.Stat
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class StatAttackSpeed : Stat(
    "attack_speed"
) {
    override fun updateStatLevel(player: Player) {
        val modifier = AttributeModifier(
            this.uuid,
            this.name,
            (1 * player.getStatLevel(this)) / 100.0,
            AttributeModifier.Operation.MULTIPLY_SCALAR_1
        )
        val instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED) ?: return
        instance.removeModifier(modifier)

        plugin.scheduler.run {
            instance.removeModifier(modifier)
            instance.addModifier(modifier)
        }
    }
}