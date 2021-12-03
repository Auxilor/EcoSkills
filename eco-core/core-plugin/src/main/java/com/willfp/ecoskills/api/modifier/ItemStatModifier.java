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
    private final double amount;

    /**
     * The slots.
     */
    private final EquipmentSlot[] slots;

    /**
     * The operation.
     */
    private final ModifierOperation operation;

    /**
     * Create a stat modifier.
     * <p>
     * Uses {@link ModifierOperation#ADD}.
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
        this(key, stat, amount, ModifierOperation.ADD, slot);
    }

    /**
     * Create a stat modifier.
     *
     * @param key       The key.
     * @param stat      The stat.
     * @param amount    The amount.
     * @param operation The operation.
     * @param slot      The slots. (Empty is the same as all).
     */
    public ItemStatModifier(@NotNull final NamespacedKey key,
                            @NotNull final Stat stat,
                            final double amount,
                            @NotNull final ModifierOperation operation,
                            @NotNull final EquipmentSlot... slot) {
        this.key = key;
        this.stat = stat;
        this.amount = amount;
        this.operation = operation;
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
    public double getAmount() {
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

    @Override
    public ModifierOperation getOperation() {
        return operation;
    }
}
