package com.xah.transition.style

enum class TransitionLevel(val code : Int,val title : String) {
    NONE_ALL(0,"无"),
    NONE(1,"回弹"),
    LOW(2,"压暗+回弹"),
    MEDIUM(3,"缩放+压暗+回弹"),
    HIGH(4,"模糊+缩放+压暗+回弹"),
}