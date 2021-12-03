package com.willfp.ecoskills.api.modifier;

import com.willfp.ecoskills.stats.Stat;
import org.bukkit.NamespacedKey;

public interface StatModifier {
    /**
     * Get the key.
     *
     * @return The key.
     */
    NamespacedKey getKey();

    /**
     * Get the stat.
     *
     * @return The stat.
     */
    Stat getStat();

    /**
     * Get the amount.
     *
     * @return The amount.
     */
    double getAmount();

    /**
     * Get the operation.
     *
     * @return The operation.
     */
    ModifierOperation getOperation();
}
