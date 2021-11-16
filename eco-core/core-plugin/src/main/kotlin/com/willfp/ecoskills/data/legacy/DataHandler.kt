package com.willfp.ecoskills.data.legacy

import java.util.UUID

interface DataHandler {
    fun <T> read(uuid: UUID, key: String): T?
    fun <T : Any> read(uuid: UUID, key: String, default: T): T {
        return read<T>(uuid, key) ?: default
    }
}