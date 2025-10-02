package com.hfut.schedule.ui.component.screen

import android.util.Log
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.hfut.schedule.ui.util.AppAnimationManager
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

enum class PartyPlace {
    TOP_CENTER,CORNER,TOP_CORNER,CENTER,FULL
}
@Composable
// 传0L则无限烟花
fun BoxScope.Party(
    timeSecond : Long = 500L,
    show : Boolean = timeSecond >= 500L,
    count : Int = 400,
    place : PartyPlace = PartyPlace.CORNER,
    modifier: Modifier = Modifier.zIndex(1f)
) {
    if(show) {
        val partyTopStart = Party(
            emitter = Emitter(duration = timeSecond).perSecond(count),
            position = Position.Relative(0.0,0.0)
        )
        val partyTopEnd = Party(
            emitter = Emitter(duration = timeSecond).perSecond(count),
            position = Position.Relative(1.0,0.0)
        )
        val partyBottomStart = Party(
            emitter = Emitter(duration = timeSecond).perSecond(count),
            position = Position.Relative(0.0,1.0)
        )
        val partyBottomEnd = Party(
            emitter = Emitter(duration = timeSecond).perSecond(count),
            position = Position.Relative(1.0,1.0)
        )
        val partyCenter = Party(
            emitter = Emitter(duration = timeSecond).perSecond(count),
            position = Position.Relative(0.5,0.5)
        )

        KonfettiView(
            modifier = modifier.fillMaxSize(),
            parties = when(place) {
                PartyPlace.FULL -> listOf(
                    partyTopStart,partyTopEnd,
                    partyBottomStart,partyBottomEnd,
                    partyCenter
                )
                PartyPlace.CENTER -> listOf(partyCenter)
                PartyPlace.CORNER -> listOf(
                    partyTopStart,partyTopEnd,
                    partyBottomStart,partyBottomEnd,
                )
                PartyPlace.TOP_CENTER -> listOf(
                    partyTopStart,
                    Party(
                        emitter = Emitter(duration = timeSecond, TimeUnit.SECONDS).perSecond(count),
                        position = Position.Relative(0.5,0.0)
                    ),
                    partyTopEnd
                )
                PartyPlace.TOP_CORNER -> listOf(
                    partyTopStart,partyTopEnd,
                )
            },
        )
    }
}


