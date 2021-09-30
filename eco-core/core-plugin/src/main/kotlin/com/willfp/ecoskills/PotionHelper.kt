package com.willfp.ecoskills

import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType

val PotionData.duration: Int
    get() {
        if (this.isExtended) {
            return when (this.type) {
                PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL -> 0
                PotionType.POISON, PotionType.REGEN -> 1800
                PotionType.SLOW_FALLING, PotionType.WEAKNESS, PotionType.SLOWNESS -> 4800
                PotionType.TURTLE_MASTER -> 800
                else -> 9600
            }
        }
        if (this.isUpgraded) {
            return when (this.type) {
                PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL -> 0
                PotionType.POISON, PotionType.REGEN -> 420
                PotionType.SLOW_FALLING, PotionType.WEAKNESS, PotionType.SLOWNESS -> 440
                PotionType.TURTLE_MASTER -> 400
                else -> 1800
            }
        }
        return when (this.type) {
            PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL -> 0
            PotionType.POISON, PotionType.REGEN -> 900
            PotionType.SLOW_FALLING, PotionType.WEAKNESS, PotionType.SLOWNESS -> 400
            PotionType.TURTLE_MASTER -> 1800
            else -> 3600
        }
    }