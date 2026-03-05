package com.xah.navigation.anim

import com.xah.navigation.model.StackEntry
import com.xah.navigation.model.ActionType

data class NavTransition(
    val type: ActionType,
    val from: StackEntry,
    val to: StackEntry,
) {
    override fun toString(): String {
        return "(type=${type.name},from=${from},to=${to})"
    }
}