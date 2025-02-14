package com.hfut.schedule.ui.utils.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
// 传0L则无限烟花
fun Party(timeSecond : Long = 1L, show : Boolean = true, content : @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if(show) {
            val partyTopStart = Party(
                emitter = Emitter(duration = timeSecond, TimeUnit.SECONDS).perSecond(30),
                position = Position.Relative(0.0,0.0)
            )
            val partyTopEnd = Party(
                emitter = Emitter(duration = timeSecond, TimeUnit.SECONDS).perSecond(30),
                position = Position.Relative(1.0,0.0)
            )
            val partyBottomStart = Party(
                emitter = Emitter(duration = timeSecond, TimeUnit.SECONDS).perSecond(30),
                position = Position.Relative(0.0,1.0)
            )
            val partyBottomEnd = Party(
                emitter = Emitter(duration = timeSecond, TimeUnit.SECONDS).perSecond(30),
                position = Position.Relative(1.0,1.0)
            )

            KonfettiView(
                modifier = Modifier.fillMaxSize().zIndex(1f),
                parties = listOf(partyTopStart,partyTopEnd,partyBottomStart,partyBottomEnd),
            )
        }
        content()
    }
}


