package com.xah.navigation.anim

import com.xah.navigation.model.dest.StackEntry
import com.xah.navigation.model.action.ActionType

data class NavTransition(
    val type: ActionType,
    val from: StackEntry,
    val to: StackEntry,
) {
    override fun toString(): String {
        return "(type=${type.name},from=${from},to=${to})"
    }
}