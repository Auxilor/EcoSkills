package com.willfp.ecoskills.skills

import com.willfp.ecoskills.SkillObject

data class SkillObjectReward (
    val obj: SkillObject,
    val options: SkillObjectOptions
) {
    override fun equals(other: Any?): Boolean {
        if (other !is SkillObjectReward) {
            return false
        }

        return this.obj == other.obj && this.options.endLevel == other.options.endLevel &&
                this.options.startLevel == other.options.startLevel
                && this.options.amountPerLevel == other.options.amountPerLevel
    }

    override fun hashCode(): Int {
        var result = obj.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }
}