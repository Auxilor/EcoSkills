package com.willfp.ecoskills.api;

import com.willfp.eco.util.NamespacedKeyUtils;
import com.willfp.ecoskills.stats.Stat;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class StatModifier {
    /**
     * The key.
     */
    private final NamespacedKey key;

    /**
     * The stat.
     */
    private final Stat stat;

    /**
     * The amount.
     */
    private final int amount;

    /**
     * The slots.
     */
    private final EquipmentSlot[] slots;

    /**
     * Create a stat modifier.
     *
     * @param key    The key.
     * @param stat   The stat.
     * @param amount The amount.
     * @param slot   The slots. (Empty is the same as all).
     */
    public StatModifier(@NotNull final NamespacedKey key,
                        @NotNull final Stat stat,
                        final int amount,
                        @NotNull final EquipmentSlot... slot) {
        this.key = key;
        this.stat = stat;
        this.amount = amount;
        this.slots = slot;
    }

    /**
     * Get the key.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Get the stat.
     *
     * @return The stat.
     */
    public Stat getStat() {
        return stat;
    }

    /**
     * Get the amount.
     *
     * @return The amount.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Get the slots.
     *
     * @return The slots.
     */
    public EquipmentSlot[] getSlots() {
        return slots;
    }
}
