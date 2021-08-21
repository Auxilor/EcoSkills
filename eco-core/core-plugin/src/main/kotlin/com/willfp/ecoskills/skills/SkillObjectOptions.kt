package com.willfp.ecoskills.skills

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
}