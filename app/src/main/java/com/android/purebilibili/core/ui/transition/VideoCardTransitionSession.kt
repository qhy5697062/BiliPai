package com.android.purebilibili.core.ui.transition

import android.os.Build
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.android.purebilibili.core.ui.adaptive.MotionTier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.pow

internal enum class VideoCardTransitionPhase {
    IDLE,
    EXPANDING,
    EXPANDED,
    COLLAPSING
}

internal data class VideoCardTransitionSession(
    val phase: VideoCardTransitionPhase = VideoCardTransitionPhase.IDLE,
    val progress: Float = 0f
)

internal enum class VideoCardTransitionDirection {
    EXPAND,
    COLLAPSE
}

internal data class VideoCardTransitionBackdropFrame(
    val active: Boolean,
    val scale: Float,
    val blurRadiusDp: Float,
    val scrimAlpha: Float
)

internal val LocalVideoCardTransitionSession = compositionLocalOf { VideoCardTransitionSession() }

internal const val VIDEO_CARD_TRANSITION_MIN_SCALE = 0.94f
private const val VIDEO_CARD_TRANSITION_MAX_SCRIM_ALPHA = 0.10f
private const val VIDEO_CARD_TRANSITION_PROGRESS_EPSILON = 0.001f
private const val VIDEO_CARD_TRANSITION_COLLAPSE_BLUR_POWER = 1.8f
private const val VIDEO_CARD_TRANSITION_EXPAND_BLUR_POWER = 1.35f
private const val VIDEO_CARD_TRANSITION_EXPAND_BLUR_PEAK_PROGRESS = 0.35f

internal fun shouldDriveVideoCardTransitionBackdrop(
    cardTransitionEnabled: Boolean,
    sharedTransitionReady: Boolean
): Boolean {
    return cardTransitionEnabled && sharedTransitionReady
}

internal fun shouldApplyVideoCardTransitionBackdropToEntry(
    cardTransitionEnabled: Boolean,
    session: VideoCardTransitionSession,
    entryInvolvesVideoDetail: Boolean,
    entryIsUnderlyingSource: Boolean
): Boolean {
    return cardTransitionEnabled &&
        entryInvolvesVideoDetail &&
        entryIsUnderlyingSource &&
        session.phase != VideoCardTransitionPhase.IDLE &&
        session.progress > VIDEO_CARD_TRANSITION_PROGRESS_EPSILON
}

internal fun resolveVideoCardTransitionBackdropFrame(
    session: VideoCardTransitionSession,
    direction: VideoCardTransitionDirection,
    skipBackdropEffects: Boolean,
    motionTier: MotionTier,
    maxBlurRadiusDp: Float,
    sdkInt: Int = Build.VERSION.SDK_INT
): VideoCardTransitionBackdropFrame {
    val progress = session.progress.coerceIn(0f, 1f)
    if (
        session.phase == VideoCardTransitionPhase.IDLE ||
        progress <= VIDEO_CARD_TRANSITION_PROGRESS_EPSILON ||
        skipBackdropEffects
    ) {
        return inactiveVideoCardTransitionBackdropFrame()
    }

    val allowBlur = motionTier != MotionTier.Reduced &&
        sdkInt >= Build.VERSION_CODES.S &&
        maxBlurRadiusDp > 0f

    val scale = resolveVideoCardTransitionScale(progress)
    val effectStrength = resolveVideoCardTransitionEffectStrength(
        progress = progress,
        direction = direction
    )
    val blurRadiusDp = if (allowBlur) {
        maxBlurRadiusDp * effectStrength
    } else {
        0f
    }
    val scrimAlpha = VIDEO_CARD_TRANSITION_MAX_SCRIM_ALPHA * effectStrength

    return VideoCardTransitionBackdropFrame(
        active = blurRadiusDp > 0f || scrimAlpha > 0f || scale < 0.999f,
        scale = scale,
        blurRadiusDp = blurRadiusDp,
        scrimAlpha = scrimAlpha
    )
}

internal fun inactiveVideoCardTransitionBackdropFrame(): VideoCardTransitionBackdropFrame {
    return VideoCardTransitionBackdropFrame(
        active = false,
        scale = 1f,
        blurRadiusDp = 0f,
        scrimAlpha = 0f
    )
}

internal fun resolveVideoCardTransitionScale(progress: Float): Float {
    val clamped = progress.coerceIn(0f, 1f)
    return 1f - ((1f - VIDEO_CARD_TRANSITION_MIN_SCALE) * clamped)
}

internal fun resolveVideoCardTransitionEffectStrength(
    progress: Float,
    direction: VideoCardTransitionDirection
): Float {
    val clamped = progress.coerceIn(0f, 1f)
    return when (direction) {
        VideoCardTransitionDirection.EXPAND -> {
            val normalized = (clamped / VIDEO_CARD_TRANSITION_EXPAND_BLUR_PEAK_PROGRESS)
                .coerceIn(0f, 1f)
            normalized.pow(VIDEO_CARD_TRANSITION_EXPAND_BLUR_POWER)
        }
        VideoCardTransitionDirection.COLLAPSE -> {
            clamped.pow(VIDEO_CARD_TRANSITION_COLLAPSE_BLUR_POWER)
        }
    }
}

@Stable
internal class VideoCardTransitionController(
    private val scope: CoroutineScope,
    private val easing: Easing,
    private val durationMillis: Int,
    private val enabled: Boolean
) {
    var session by mutableStateOf(VideoCardTransitionSession())
        private set

    private val progressAnimatable = Animatable(0f)
    private var runningJob: Job? = null

    fun beginExpand() {
        if (!enabled) return
        runningJob?.cancel()
        runningJob = scope.launch {
            session = VideoCardTransitionSession(
                phase = VideoCardTransitionPhase.EXPANDING,
                progress = 0f
            )
            progressAnimatable.snapTo(0f)
            progressAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMillis, easing = easing)
            )
            session = VideoCardTransitionSession(
                phase = VideoCardTransitionPhase.EXPANDED,
                progress = 1f
            )
        }
    }

    fun beginCollapse(skipBackdropEffects: Boolean) {
        if (!enabled) {
            reset()
            return
        }
        runningJob?.cancel()
        runningJob = scope.launch {
            session = session.copy(
                phase = VideoCardTransitionPhase.COLLAPSING,
                progress = progressAnimatable.value.coerceIn(0f, 1f)
            )
            if (skipBackdropEffects) {
                progressAnimatable.snapTo(0f)
            } else {
                progressAnimatable.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = durationMillis, easing = easing)
                )
            }
            reset()
        }
    }

    fun syncPredictiveExpandedFraction(expandedFraction: Float) {
        if (!enabled) return
        runningJob?.cancel()
        val clamped = expandedFraction.coerceIn(0f, 1f)
        scope.launch {
            progressAnimatable.snapTo(clamped)
            session = when {
                clamped <= VIDEO_CARD_TRANSITION_PROGRESS_EPSILON ->
                    VideoCardTransitionSession(VideoCardTransitionPhase.IDLE, 0f)
                clamped >= 1f - VIDEO_CARD_TRANSITION_PROGRESS_EPSILON ->
                    VideoCardTransitionSession(VideoCardTransitionPhase.EXPANDED, 1f)
                else ->
                    VideoCardTransitionSession(VideoCardTransitionPhase.COLLAPSING, clamped)
            }
        }
    }

    fun reset() {
        runningJob?.cancel()
        runningJob = null
        scope.launch {
            progressAnimatable.snapTo(0f)
            session = VideoCardTransitionSession()
        }
    }
}

@Composable
internal fun rememberVideoCardTransitionController(
    enabled: Boolean,
    speedSettings: VideoSharedTransitionSpeedSettings
): VideoCardTransitionController {
    val scope = rememberCoroutineScope()
    val easing = remember { resolveVideoCardSharedTransitionEasing() }
    val durationMillis = remember(speedSettings) {
        resolveVideoSharedTransitionDurationMillis(speedSettings)
    }
    return remember(enabled, durationMillis) {
        VideoCardTransitionController(
            scope = scope,
            easing = easing,
            durationMillis = durationMillis,
            enabled = enabled
        )
    }
}