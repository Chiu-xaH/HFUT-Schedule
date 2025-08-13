package com.xah.transition.style

enum class TransitionLevel(val code : Int,val title : String) {
    NONE(0,"回弹"),
    LOW(1,"压暗+回弹"),
    MEDIUM(2,"缩放+压暗+回弹"),
    HIGH(3,"模糊+缩放+压暗+回弹")
}