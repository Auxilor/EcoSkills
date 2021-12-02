package com.willfp.ecoskills.api.modifier;

public enum ModifierOperation {
    /**
     * Add a specific number to a stat.
     */
    ADD,
    
    /**
     * Multiply a stat by some value.
     * <p>
     * Applies after adding.
     */
    MULTIPLY
}
