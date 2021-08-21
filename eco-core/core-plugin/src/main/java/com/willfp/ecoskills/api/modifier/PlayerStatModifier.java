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
    private final int amount;

    /**
     * Create a stat modifier.
     *
     * @param key    The key.
     * @param stat   The stat.
     * @param amount The amount.
     */
    public PlayerStatModifier(@NotNull final NamespacedKey key,
                              @NotNull final Stat stat,
                              final int amount) {
        this.key = key;
        this.stat = stat;
        this.amount = amount;
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
}
