package com.willfp.ecoskills.integrations.customblocks

import dev.lone.itemsadder.api.CustomBlock

class CustomBlocksItemsAdder: CustomBlocksWrapper {
    override fun exists(id: String): Boolean {
        return CustomBlock.getInstance(id) != null
    }
}