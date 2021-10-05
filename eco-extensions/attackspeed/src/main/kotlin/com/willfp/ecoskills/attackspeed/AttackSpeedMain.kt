package com.willfp.ecoskills.attackspeed

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.extensions.Extension

class AttackSpeedMain(plugin: EcoPlugin) : Extension(plugin) {
    val attackSpeed = StatAttackSpeed()

    override fun onEnable() {
        // Do nothing
    }

    override fun onDisable() {
        // Do nothing
    }
}