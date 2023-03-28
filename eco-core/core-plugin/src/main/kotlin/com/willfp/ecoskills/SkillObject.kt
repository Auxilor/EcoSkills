package com.willfp.ecoskills

import com.willfp.eco.core.registry.Registrable

abstract class SkillObject(
    val id: String
) : Registrable {
    override fun getID(): String {
        return this.id
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SkillObject) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
