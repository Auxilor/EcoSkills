package com.willfp.ecoskills.api.modifier;

import com.willfp.ecoskills.stats.Stat;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class PlayerStatModifier implements StatModifier {
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
     */
    public PlayerStatModifier(@NotNull final NamespacedKey key,
                              @NotNull final Stat stat,
                              final int amount) {
        this(key, stat, amount, ModifierOperation.ADD);
    }

    /**
     * Create a stat modifier.
     *
     * @param key       The key.
     * @param stat      The stat.
     * @param amount    The amount.
     * @param operation The operation.
     */
    public PlayerStatModifier(@NotNull final NamespacedKey key,
                              @NotNull final Stat stat,
                              final double amount,
                              @NotNull final ModifierOperation operation) {
        this.key = key;
        this.stat = stat;
        this.amount = amount;
        this.operation = operation;
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

    @Override
    public ModifierOperation getOperation() {
        return operation;
    }
}
