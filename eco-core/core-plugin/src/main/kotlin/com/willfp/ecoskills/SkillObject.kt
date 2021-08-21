package com.willfp.ecoskills

abstract class SkillObject(
    val id: String
){
    override fun equals(other: Any?): Boolean {
        if (other !is SkillObject) {
            return false
        }

        return this.id == other.id
    }
}