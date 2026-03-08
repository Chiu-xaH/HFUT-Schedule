package com.xah.navigation.controller

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.xah.container.controller.SharedRegistry
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.anim.NavTransition
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.dest.Destination
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.model.dest.StackEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID


class NavigationController(
    private val scope: CoroutineScope,
    private val startDestination: Destination,
    private val _stack: SnapshotStateList<StackEntry>,
    val sharedRegistry : SharedRegistry? = null,
) {
    val stack: List<StackEntry> get() = _stack

    var navTransition by mutableStateOf<NavTransition?>(null)
        private set

    var isTransitioning by mutableStateOf(false)
        private set

    var transitionLevel by mutableStateOf(EffectLevel.FULL)

    val transitionProgress = Animatable(0f)

    private val animationSpecSharedTween = 500
    val defaultSpecWithTinyScale = tween<Float>(250)
    val defaultSpecWithShared = tween<Float>(animationSpecSharedTween*8/5)
    val defaultSpec = tween<Float>(animationSpecSharedTween*13/10)

    private fun createAndPush(
        destination : Destination
    ) {
        val newEntry = StackEntry(
            id = UUID.randomUUID().toString(),
            destination = destination
        )
        _stack += newEntry
    }


    private fun pushInternal(
        destination: Destination,
        launchMode: LaunchMode = LaunchMode.Push(true),
    ) {
        scope.launch {
            val from = _stack.last()

            when (launchMode) {
                is LaunchMode.Push -> {
                    if(launchMode.reuse) {
                        // 如果栈顶是目标项目，则复用
                        if (_stack.isNotEmpty() && _stack.last().destination == destination) {
                            // 如果栈顶就是目标，保持栈顶不变
                            return@launch
                        } else {
                            createAndPush(destination)
                        }
                    } else {
                        // 每次都创建新的并加入栈
                        createAndPush(destination)
                    }
                }
                is LaunchMode.Single -> {
                    if(launchMode.reuse) {
                        // 栈内存在则复用并清空其余项，没有则直接CLEAR_STACK
                        // 从栈底（索引0）开始寻找
                        val existingIndex = _stack.indexOfFirst { it.destination == destination }
                        if (existingIndex != -1) {
                            // 目标项已经存在，复用
                            val item = _stack[existingIndex]
                            _stack.clear()
                            _stack.add(item)
                        } else {
                            // 如果栈中没有该目标，直接清空栈并压入
                            _stack.clear()
                            createAndPush(destination)
                        }
                    } else {
                        // 清空栈并压入
                        _stack.clear()
                        createAndPush(destination)
                    }
                }
                is LaunchMode.PopToExisting -> {
                    // 如果栈中已经有该目标，则清除其之上的所有栈并复用它
                    val existingIndex = _stack.indexOfFirst { it.destination == destination }
                    if (existingIndex != -1) {
                        _stack.subList(existingIndex + 1, _stack.size).clear() // 清除目标 Activity 之上的所有元素
                    } else {
                        launchMode.actionType = ActionType.PUSH
                        createAndPush(destination)
                    }
                }
            }
            // 动画未进行时归位，不影响打断动画
            val type = launchMode.actionType
            snap(type)
            // 添加过渡动画
            navTransition = NavTransition(
                type = type,
                from = from,
                to = _stack.last()
            )
        }
    }

    private fun popInternal() {
        scope.launch {
            if (_stack.size <= 1) return@launch

            val from = _stack.last()
            val to = _stack[_stack.lastIndex - 1]
            // 动画未进行时归位，不影响打断动画
            val type = ActionType.POP
            snap(type)
            navTransition = NavTransition(
                type = type,
                from = from,
                to = to,
            )
        }
    }

    // 动画未进行时归位，不影响打断动画
    private suspend fun snap(
        type : ActionType
    ) {
        if(!isTransitioning) {
            transitionProgress.snapTo(
                when(type) {
                    ActionType.PUSH -> 0f
                    ActionType.POP -> 1f
                }
            )
        }
    }


    fun animate(
        animationSpec: AnimationSpec<Float> = defaultSpec
    ) {
        scope.launch {
            internalAnimate(animationSpec)
        }
    }


    private suspend fun internalAnimate(
        animationSpec: AnimationSpec<Float> = defaultSpec
    ) {
        navTransition ?: return

        val target = when (navTransition!!.type) {
            ActionType.PUSH -> 1f
            ActionType.POP -> 0f
        }

        // 设置标志位，开始动画
        isTransitioning = true
        transitionProgress.animateTo(targetValue = target, animationSpec = animationSpec)

        // 移除栈，置状态
        when (navTransition?.type) {
            ActionType.PUSH -> Unit
            ActionType.POP -> {
                if(_stack.size > 1) {
                    _stack.removeAt(_stack.size-1)
                }
            }
            null -> Unit
        }
        navTransition = null
        isTransitioning = false
    }

    fun push(
        destination: Destination,
        launchMode: LaunchMode = LaunchMode.Push(reuse = true),
    ) {
        val registry = this.sharedRegistry
        if(this.transitionLevel == EffectLevel.NONE || registry == null) {
            this.pushInternal(destination,launchMode)
        } else {
            registry.push(
                destination.key,
                onAnimatedFinished = {
                    snapshotFlow { this.isTransitioning }
                        .filter { !it }
                        .first()
                }
            ) {
                this.pushInternal(destination,launchMode)
            }
        }
    }

    fun pop() {
        val registry = this.sharedRegistry
        if(this.transitionLevel == EffectLevel.NONE || registry == null) {
            this.popInternal()
        } else {
            registry.pop(
                this.stack.last().destination.key,
                onAnimatedFinished = {
                    snapshotFlow { this.isTransitioning }
                        .filter { !it }
                        .first()
                }
            ) {
                this.popInternal()
            }
        }
    }

    init {
        if(_stack.isEmpty()) {
            createAndPush(startDestination)
        }
    }
}