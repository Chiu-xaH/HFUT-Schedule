package com.xah.navigation.model.action

sealed class LaunchMode(open var actionType: ActionType) {
    /**
     * 压入到栈顶，如果reuse则检查栈顶是否已有实例，有则复用
     * @param reuse 复用
     */
    data class Push(val reuse : Boolean) : LaunchMode(ActionType.PUSH)
    /**
     * 检查栈内是否已有实例，有则复用并清除其顶部项,没有则Push
     */
    data object PopToExisting: LaunchMode(ActionType.POP)
    /**
     * 栈内只有一个项目，如果reuse则检查栈顶是否已有实例，有则复用，并将其余所有项目清除；如果reuse=false则直接清空栈并压入
     * @param reuse 复用
     */
    data class Single(
        val reuse : Boolean,
        override var actionType: ActionType = ActionType.PUSH
    ) : LaunchMode(actionType)
}