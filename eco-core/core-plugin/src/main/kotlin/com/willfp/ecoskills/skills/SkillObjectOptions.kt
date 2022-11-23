package com.willfp.ecoskills.skills

import java.util.Objects

class SkillObjectOptions(
    options: String
) {
    val amountPerLevel: Int
    val startLevel: Int
    val endLevel: Int

    init {
        val split = options.split(":")
        amountPerLevel = split[0].toInt()
        if (split.size == 3) {
            startLevel = split[1].toInt()
            endLevel = split[2].toInt()
        } else {
            startLevel = 1
            endLevel = Int.MAX_VALUE
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SkillObjectOptions) {
            return false
        }

        return this.amountPerLevel == other.amountPerLevel
                && this.startLevel == other.startLevel
                && this.endLevel == other.endLevel
    }

    override fun hashCode(): Int {
        return Objects.hash(
            this.amountPerLevel,
            this.startLevel,
            this.endLevel
        )
    }
}
