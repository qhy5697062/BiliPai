package com.android.purebilibili.feature.settings

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import com.android.purebilibili.core.ui.motion.AppMotionEasing

internal const val SETTINGS_ROOT_CATEGORY_FADE_MILLIS = 220

internal sealed interface SettingsRootBodyDestination {
    data object Home : SettingsRootBodyDestination
    data class Category(val category: SettingsRootCategory) : SettingsRootBodyDestination
    data object Search : SettingsRootBodyDestination
}

internal fun resolveSettingsRootBodyDestination(
    searchQuery: String,
    activeCategory: SettingsRootCategory?
): SettingsRootBodyDestination = when {
    searchQuery.isNotBlank() -> SettingsRootBodyDestination.Search
    activeCategory != null -> SettingsRootBodyDestination.Category(activeCategory)
    else -> SettingsRootBodyDestination.Home
}

internal fun resolveSettingsRootCategoryFadeMillis(
    animationEnabled: Boolean,
    reduceMotion: Boolean
): Int = if (!animationEnabled || reduceMotion) 0 else SETTINGS_ROOT_CATEGORY_FADE_MILLIS

internal fun resolveSettingsRootCategoryContentTransform(
    animationEnabled: Boolean,
    reduceMotion: Boolean
): ContentTransform {
    val millis = resolveSettingsRootCategoryFadeMillis(animationEnabled, reduceMotion)
    if (millis <= 0) {
        return EnterTransition.None togetherWith ExitTransition.None
    }
    return fadeIn(
        animationSpec = tween(
            durationMillis = millis,
            easing = AppMotionEasing.EmphasizedEnter
        )
    ) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = millis,
            easing = AppMotionEasing.EmphasizedExit
        )
    )
}