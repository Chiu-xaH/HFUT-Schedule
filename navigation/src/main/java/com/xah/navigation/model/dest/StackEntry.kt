package com.xah.navigation.model.dest

class StackEntry(
    val id: String,
    val destination: Destination
) {
    override fun toString(): String {
        return "Destination(key=${destination.key})"
    }
}