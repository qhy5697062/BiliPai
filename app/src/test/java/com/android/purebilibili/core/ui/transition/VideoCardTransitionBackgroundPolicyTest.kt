package com.android.purebilibili.core.ui.transition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoCardTransitionBackgroundPolicyTest {

    @Test
    fun api35OpeningFrameUsesOriginalBlurStrengthAndScrim() {
        val frame = resolveVideoCardTransitionBackgroundFrame(
            progress = 1f,
            phase = VideoCardTransitionBackgroundPhase.OPENING,
            sdkInt = 35
        )

        assertEquals(36f, frame.blurRadiusPx)
        assertEquals(0f, frame.blurRadiusPx % 2f)
        assertEquals(0.22f, frame.scrimAlpha)
        assertTrue(frame.contentScale < 1f)
    }

    @Test
    fun returningFrameFadesBlurWithoutScrimOrScale() {
        val start = resolveVideoCardTransitionBackgroundFrame(
            progress = 1f,
            phase = VideoCardTransitionBackgroundPhase.RETURNING,
            sdkInt = 35
        )
        val middle = resolveVideoCardTransitionBackgroundFrame(
            progress = 0.5f,
            phase = VideoCardTransitionBackgroundPhase.RETURNING,
            sdkInt = 35
        )
        val end = resolveVideoCardTransitionBackgroundFrame(
            progress = 0f,
            phase = VideoCardTransitionBackgroundPhase.RETURNING,
            sdkInt = 35
        )

        assertTrue(start.blurRadiusPx > middle.blurRadiusPx)
        assertTrue(middle.blurRadiusPx > end.blurRadiusPx)
        assertEquals(28f, middle.blurRadiusPx)
        assertEquals(0f, start.scrimAlpha)
        assertEquals(0f, middle.scrimAlpha)
        assertEquals(1f, start.contentScale)
        assertEquals(1f, middle.contentScale)
        assertEquals(0f, end.blurRadiusPx)
    }

    @Test
    fun androidBeforeSDisablesRealtimeBlurButKeepsOpeningScrim() {
        val opening = resolveVideoCardTransitionBackgroundFrame(
            progress = 1f,
            phase = VideoCardTransitionBackgroundPhase.OPENING,
            sdkInt = 30
        )
        val returning = resolveVideoCardTransitionBackgroundFrame(
            progress = 1f,
            phase = VideoCardTransitionBackgroundPhase.RETURNING,
            sdkInt = 30
        )

        assertEquals(0f, opening.blurRadiusPx)
        assertTrue(opening.scrimAlpha > 0f)
        assertEquals(0f, returning.blurRadiusPx)
        assertEquals(0f, returning.scrimAlpha)
    }

    @Test
    fun lowProgressKeepsBlurVisibleUntilTransitionEnds() {
        val frame = resolveVideoCardTransitionBackgroundFrame(
            progress = 0.25f,
            phase = VideoCardTransitionBackgroundPhase.RETURNING,
            sdkInt = 35
        )

        assertEquals(16f, frame.blurRadiusPx)
        assertEquals(0f, frame.scrimAlpha)
        assertEquals(1f, frame.contentScale)
    }

    @Test
    fun routeMatcherTargetsOnlyRecordedSourceEntryOrActiveMainHostPage() {
        assertTrue(
            shouldApplyVideoCardTransitionBackgroundToRoute(
                entryRoute = "main_host",
                sourceRoute = "home",
                activeMainHostRoute = "home"
            )
        )
        assertFalse(
            shouldApplyVideoCardTransitionBackgroundToRoute(
                entryRoute = "main_host",
                sourceRoute = "home",
                activeMainHostRoute = "dynamic"
            )
        )
        assertTrue(
            shouldApplyVideoCardTransitionBackgroundToRoute(
                entryRoute = "search",
                sourceRoute = "search",
                activeMainHostRoute = "home"
            )
        )
        assertTrue(
            shouldApplyVideoCardTransitionBackgroundToRoute(
                entryRoute = "space/123",
                sourceRoute = "space/123?from=archive",
                activeMainHostRoute = "home"
            )
        )
        assertFalse(
            shouldApplyVideoCardTransitionBackgroundToRoute(
                entryRoute = "settings",
                sourceRoute = "home",
                activeMainHostRoute = "home"
            )
        )
        assertFalse(
            shouldApplyVideoCardTransitionBackgroundToRoute(
                entryRoute = "video/BV1",
                sourceRoute = "video",
                activeMainHostRoute = "home"
            )
        )
    }
}
