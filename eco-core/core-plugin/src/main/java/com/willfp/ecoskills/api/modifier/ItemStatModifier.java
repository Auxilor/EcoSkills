package com.willfp.ecoskills.api.modifier;

import com.willfp.ecoskills.stats.Stat;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class ItemStatModifier implements StatModifier {
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
    public ItemStatModifier(@NotNull final NamespacedKey key,
                            @NotNull final Stat stat,
                            final int amount,
                            @NotNull final EquipmentSlot... slot) {
        this.key = key;
        this.stat = stat;
        this.amount = amount;
        this.slots = slot.length == 0 ? EquipmentSlot.values() : slot;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    public Stat getStat() {
        return stat;
    }

    @Override
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
