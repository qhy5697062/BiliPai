package com.android.purebilibili.core.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.android.purebilibili.core.theme.AndroidNativeVariant
import com.android.purebilibili.core.theme.LocalAndroidNativeVariant
import com.android.purebilibili.core.theme.LocalUiPreset
import com.android.purebilibili.core.theme.UiPreset
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Preset-aware surface tokens. Replace direct reads of `MaterialTheme.colorScheme.surface`
 * / `.background` in feature screens with these accessors so AMOLED, dynamic-color, and
 * Miuix bridging stay consistent across presets.
 *
 * - iOS:   card = white surface, grouped list = iOSSystemGray6 background.
 * - MD3:   card = surfaceContainer, grouped list = background.
 * - Miuix: card = surfaceContainer (bridge maps it to Miuix's secondaryContainerVariant).
 */
object AppSurfaceTokens {

    fun resolveCardContainer(
        colorScheme: ColorScheme,
        uiPreset: UiPreset,
        androidNativeVariant: AndroidNativeVariant
    ): Color = when (uiPreset) {
        UiPreset.IOS -> colorScheme.surface
        UiPreset.MD3 -> colorScheme.surfaceContainer
    }

    fun resolveGroupedListContainer(
        colorScheme: ColorScheme,
        uiPreset: UiPreset,
        @Suppress("UNUSED_PARAMETER") androidNativeVariant: AndroidNativeVariant
    ): Color = when (uiPreset) {
        UiPreset.IOS -> colorScheme.background
        UiPreset.MD3 -> colorScheme.background
    }

    fun resolveChromeBackground(
        colorScheme: ColorScheme,
        uiPreset: UiPreset,
        @Suppress("UNUSED_PARAMETER") androidNativeVariant: AndroidNativeVariant
    ): Color = colorScheme.background

    fun resolveDivider(
        colorScheme: ColorScheme,
        @Suppress("UNUSED_PARAMETER") uiPreset: UiPreset,
        @Suppress("UNUSED_PARAMETER") androidNativeVariant: AndroidNativeVariant
    ): Color = colorScheme.outlineVariant

    @Composable
    @ReadOnlyComposable
    fun cardContainer(): Color = resolveCardContainer(
        colorScheme = MaterialTheme.colorScheme,
        uiPreset = LocalUiPreset.current,
        androidNativeVariant = LocalAndroidNativeVariant.current
    )

    @Composable
    @ReadOnlyComposable
    fun groupedListContainer(): Color = resolveGroupedListContainer(
        colorScheme = MaterialTheme.colorScheme,
        uiPreset = LocalUiPreset.current,
        androidNativeVariant = LocalAndroidNativeVariant.current
    )

    @Composable
    @ReadOnlyComposable
    fun chromeBackground(): Color = resolveChromeBackground(
        colorScheme = MaterialTheme.colorScheme,
        uiPreset = LocalUiPreset.current,
        androidNativeVariant = LocalAndroidNativeVariant.current
    )

    @Composable
    @ReadOnlyComposable
    fun divider(): Color = resolveDivider(
        colorScheme = MaterialTheme.colorScheme,
        uiPreset = LocalUiPreset.current,
        androidNativeVariant = LocalAndroidNativeVariant.current
    )

    @Composable
    @ReadOnlyComposable
    fun onSurfaceVariantSummary(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        materialFallback = MaterialTheme.colorScheme.onSurfaceVariant
    )

    @Composable
    @ReadOnlyComposable
    fun onSurfaceVariantActions(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.onSurfaceVariantActions,
        materialFallback = MaterialTheme.colorScheme.onSurfaceVariant
    )

    @Composable
    @ReadOnlyComposable
    fun secondaryContainer(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.secondaryContainer,
        materialFallback = MaterialTheme.colorScheme.secondaryContainer
    )

    @Composable
    @ReadOnlyComposable
    fun onSecondaryContainer(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.onSecondaryContainer,
        materialFallback = MaterialTheme.colorScheme.onSecondaryContainer
    )

    @Composable
    @ReadOnlyComposable
    fun surface(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.surface,
        materialFallback = MaterialTheme.colorScheme.surface
    )

    @Composable
    @ReadOnlyComposable
    fun surfaceContainer(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.surfaceContainer,
        materialFallback = MaterialTheme.colorScheme.surfaceContainer
    )

    @Composable
    @ReadOnlyComposable
    fun surfaceContainerHigh(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.surfaceContainerHigh,
        materialFallback = MaterialTheme.colorScheme.surfaceContainerHigh
    )

    @Composable
    @ReadOnlyComposable
    fun onSurface(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.onSurface,
        materialFallback = MaterialTheme.colorScheme.onSurface
    )

    @Composable
    @ReadOnlyComposable
    fun onSurfaceContainerHigh(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.onSurfaceContainerHigh,
        materialFallback = MaterialTheme.colorScheme.onSurfaceVariant
    )

    @Composable
    @ReadOnlyComposable
    fun onSurfaceContainerHighest(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.onSurfaceContainerHighest,
        materialFallback = MaterialTheme.colorScheme.onSurfaceVariant
    )

    @Composable
    @ReadOnlyComposable
    fun primary(): Color = resolveMiuixSemanticColorComposable(
        miuixColor = MiuixTheme.colorScheme.primary,
        materialFallback = MaterialTheme.colorScheme.primary
    )

    fun resolveMiuixSemanticColor(
        isMiuix: Boolean,
        miuixColor: Color,
        materialFallback: Color
    ): Color = if (isMiuix) miuixColor else materialFallback

    @Composable
    @ReadOnlyComposable
    private fun resolveMiuixSemanticColorComposable(
        miuixColor: Color,
        materialFallback: Color
    ): Color {
        val uiPreset = LocalUiPreset.current
        val androidNativeVariant = LocalAndroidNativeVariant.current
        return resolveMiuixSemanticColor(
            isMiuix = isNativeMiuixEnabled(uiPreset, androidNativeVariant),
            miuixColor = miuixColor,
            materialFallback = materialFallback
        )
    }
}
