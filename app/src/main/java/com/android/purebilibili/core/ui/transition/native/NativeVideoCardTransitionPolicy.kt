package com.android.purebilibili.core.ui.transition.native

import android.os.Build
import kotlin.math.roundToInt

internal data class NativeVideoTransitionRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float
        get() = right - left

    val height: Float
        get() = bottom - top

    fun isUsable(): Boolean {
        return width > 1f && height > 1f
    }
}

internal enum class NativeVideoCardTransitionPhase {
    Closing
}

internal data class NativeVideoCardTransitionSpec(
    val maxBlurRadiusPx: Float = NATIVE_VIDEO_CARD_TRANSITION_MAX_BLUR_RADIUS_PX,
    val maxScrimAlpha: Float = NATIVE_VIDEO_CARD_TRANSITION_MAX_SCRIM_ALPHA
)

internal data class NativeVideoCardTransitionFrame(
    val blurRadiusPx: Float,
    val scrimAlpha: Float
)

internal const val NATIVE_VIDEO_CARD_TRANSITION_DURATION_MILLIS = 420L
internal const val NATIVE_VIDEO_CARD_TRANSITION_MAX_BLUR_RADIUS_PX = 36f
private const val NATIVE_VIDEO_CARD_TRANSITION_BLUR_QUANTUM_PX = 2f
private const val NATIVE_VIDEO_CARD_TRANSITION_MAX_SCRIM_ALPHA = 0.34f

internal fun resolveNativeVideoCardTransitionFrame(
    spec: NativeVideoCardTransitionSpec,
    progress: Float,
    phase: NativeVideoCardTransitionPhase,
    sdkInt: Int = Build.VERSION.SDK_INT
): NativeVideoCardTransitionFrame {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val effectStrength = resolveNativeVideoCardTransitionEffectStrength(clampedProgress, phase)
    val rawBlurRadiusPx = if (sdkInt >= Build.VERSION_CODES.S) {
        spec.maxBlurRadiusPx.coerceAtLeast(0f) * effectStrength
    } else {
        0f
    }

    return NativeVideoCardTransitionFrame(
        blurRadiusPx = quantizeNativeVideoCardTransitionBlurRadius(rawBlurRadiusPx),
        scrimAlpha = resolveNativeVideoCardTransitionScrimAlpha(
            spec = spec,
            effectStrength = effectStrength,
            phase = phase
        )
    )
}

private fun resolveNativeVideoCardTransitionScrimAlpha(
    spec: NativeVideoCardTransitionSpec,
    effectStrength: Float,
    phase: NativeVideoCardTransitionPhase
): Float {
    return when (phase) {
        NativeVideoCardTransitionPhase.Closing -> 0f
    }.coerceAtMost(spec.maxScrimAlpha.coerceIn(0f, 1f) * effectStrength)
}

private fun resolveNativeVideoCardTransitionEffectStrength(
    progress: Float,
    phase: NativeVideoCardTransitionPhase
): Float {
    val clamped = progress.coerceIn(0f, 1f)
    return when (phase) {
        NativeVideoCardTransitionPhase.Closing -> (1f - clamped * clamped)
    }
}

private fun quantizeNativeVideoCardTransitionBlurRadius(radiusPx: Float): Float {
    if (radiusPx <= 0f) return 0f
    return ((radiusPx / NATIVE_VIDEO_CARD_TRANSITION_BLUR_QUANTUM_PX).roundToInt() *
        NATIVE_VIDEO_CARD_TRANSITION_BLUR_QUANTUM_PX)
        .coerceIn(0f, NATIVE_VIDEO_CARD_TRANSITION_MAX_BLUR_RADIUS_PX)
}
