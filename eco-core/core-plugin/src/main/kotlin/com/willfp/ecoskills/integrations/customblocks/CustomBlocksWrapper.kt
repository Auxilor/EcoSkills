package com.willfp.ecoskills.integrations.customblocks

interface CustomBlocksWrapper {
    /**
     * Get if a custom block with that id exists.
     *
     * @returns true if a custom block with that ID exists, false otherwise.
     */
    fun exists(id: String): Boolean
}