package com.willfp.ecoskills.util;

import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class PotionUtils {
    /**
     * Get potion duration from data.
     *
     * @param data The data.
     * @return The duration in ticks.
     */
    public static int getDuration(@NotNull final PotionData data) {
        if (data.isExtended()) {
            return switch (data.getType()) {
                case POISON, REGEN -> 1800;
                case SLOW_FALLING, WEAKNESS, SLOWNESS -> 4800;
                case TURTLE_MASTER -> 800;
                default -> 9600;
            };
        } else if (data.isUpgraded()) {
            return switch (data.getType()) {
                case POISON -> 420;
                case REGEN -> 440;
                case TURTLE_MASTER, SLOWNESS -> 400;
                default -> 1800;
            };
        } else {
            return switch (data.getType()) {
                case POISON, REGEN -> 900;
                case TURTLE_MASTER -> 400;
                case SLOW_FALLING, WEAKNESS, SLOWNESS -> 1800;
                default -> 3600;
            };
        }
    }
}
