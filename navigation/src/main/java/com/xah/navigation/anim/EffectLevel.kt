package com.xah.navigation.anim

enum class EffectLevel(val levelNum : Int) {
    /**
     * 背景模糊、压暗、缩放；前景模糊、缩放
     */
    FULL(3),
    /**
     * 背景缩放、压暗；前景缩放
     */
    NO_BLUR(2),
    /**
     * 背景压暗；前景缩放
     */
    NO_SCALE(1),
    /**
     * 背景无；前景轻缩放、透明度淡入
     */
    NONE(0)
}