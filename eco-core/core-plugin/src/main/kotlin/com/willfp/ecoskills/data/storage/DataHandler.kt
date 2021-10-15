package com.willfp.ecoskills.data.storage

import java.util.*

interface DataHandler {
    fun save()

    fun <T> write(uuid: UUID, key: String, value: T)
    fun <T : Any> read(uuid: UUID, key: String, default: T): T
}