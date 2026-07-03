package com.android.purebilibili.core.ui.transition.native

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NativeVideoCardTransitionPolicyTest {

    private val spec = NativeVideoCardTransitionSpec(maxBlurRadiusPx = 28f)

    @Test
    fun api31ReturnBackgroundBlurClearsAsHomeBecomesVisible() {
        val start = resolveNativeVideoCardTransitionFrame(
            spec = spec,
            progress = 0f,
            phase = NativeVideoCardTransitionPhase.Closing,
            sdkInt = 31
        )
        val middle = resolveNativeVideoCardTransitionFrame(
            spec = spec,
            progress = 0.5f,
            phase = NativeVideoCardTransitionPhase.Closing,
            sdkInt = 31
        )
        val end = resolveNativeVideoCardTransitionFrame(
            spec = spec,
            progress = 1f,
            phase = NativeVideoCardTransitionPhase.Closing,
            sdkInt = 31
        )

        assertTrue(start.blurRadiusPx > middle.blurRadiusPx)
        assertTrue(middle.blurRadiusPx > end.blurRadiusPx)
        assertEquals(28f, start.blurRadiusPx)
        assertEquals(22f, middle.blurRadiusPx)
        assertEquals(0f, start.scrimAlpha)
        assertEquals(0f, middle.scrimAlpha)
        assertEquals(0f, end.scrimAlpha)
    }

    @Test
    fun nativeFrameQuantizesBlurAndDoesNotDrawReturnScrim() {
        val middle = resolveNativeVideoCardTransitionFrame(
            spec = spec,
            progress = 0.5f,
            phase = NativeVideoCardTransitionPhase.Closing,
            sdkInt = 31
        )

        assertTrue(middle.blurRadiusPx > 0f)
        assertEquals(0f, middle.blurRadiusPx % 2f)
        assertEquals(22f, middle.blurRadiusPx)
        assertEquals(0f, middle.scrimAlpha)
    }

    @Test
    fun api30KeepsBlurAndReturnScrimDisabled() {
        val middle = resolveNativeVideoCardTransitionFrame(
            spec = spec,
            progress = 0.5f,
            phase = NativeVideoCardTransitionPhase.Closing,
            sdkInt = 30
        )

        assertEquals(0f, middle.blurRadiusPx)
        assertEquals(0f, middle.scrimAlpha)
    }

    @Test
    fun defaultMaxBlurUsesOriginalThirtySixPixels() {
        val start = resolveNativeVideoCardTransitionFrame(
            spec = NativeVideoCardTransitionSpec(
                maxBlurRadiusPx = NATIVE_VIDEO_CARD_TRANSITION_MAX_BLUR_RADIUS_PX
            ),
            progress = 0f,
            phase = NativeVideoCardTransitionPhase.Closing,
            sdkInt = 35
        )

        assertEquals(36f, start.blurRadiusPx)
        assertEquals(0f, start.scrimAlpha)
    }
}
