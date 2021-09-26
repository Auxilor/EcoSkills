package com.willfp.ecoskills.data.storage

import java.util.*

interface DataHandler {
    fun save()

    fun <T> write(uuid: UUID, key: String, value: T)

    fun readInt(uuid: UUID, key: String): Int
    fun readDouble(uuid: UUID, key: String): Double
    fun readString(uuid: UUID, key: String, default: String = ""): String
}