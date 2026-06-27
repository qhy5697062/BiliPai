package com.android.purebilibili.feature.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SettingsRootCategoryTransitionPolicyTest {

    @Test
    fun resolveSettingsRootBodyDestination_prefersSearchOverCategory() {
        val destination = resolveSettingsRootBodyDestination(
            searchQuery = "主题",
            activeCategory = SettingsRootCategory.APPEARANCE_INTERACTION
        )

        assertIs<SettingsRootBodyDestination.Search>(destination)
    }

    @Test
    fun resolveSettingsRootBodyDestination_usesCategoryWhenNotSearching() {
        val destination = resolveSettingsRootBodyDestination(
            searchQuery = "",
            activeCategory = SettingsRootCategory.CONTENT_PLAYBACK
        )

        val category = assertIs<SettingsRootBodyDestination.Category>(destination)
        assertEquals(SettingsRootCategory.CONTENT_PLAYBACK, category.category)
    }

    @Test
    fun resolveSettingsRootBodyDestination_defaultsToHome() {
        val destination = resolveSettingsRootBodyDestination(
            searchQuery = "",
            activeCategory = null
        )

        assertIs<SettingsRootBodyDestination.Home>(destination)
    }

    @Test
    fun resolveSettingsRootCategoryFadeMillis_respectsAnimationGate() {
        assertEquals(0, resolveSettingsRootCategoryFadeMillis(animationEnabled = false, reduceMotion = false))
        assertEquals(0, resolveSettingsRootCategoryFadeMillis(animationEnabled = true, reduceMotion = true))
        assertEquals(
            SETTINGS_ROOT_CATEGORY_FADE_MILLIS,
            resolveSettingsRootCategoryFadeMillis(animationEnabled = true, reduceMotion = false)
        )
    }

    @Test
    fun resolveSettingsRootCategoryContentTransform_disablesFadeWhenAnimationGateOff() {
        assertEquals(
            0,
            resolveSettingsRootCategoryFadeMillis(animationEnabled = false, reduceMotion = false)
        )
        assertEquals(
            0,
            resolveSettingsRootCategoryFadeMillis(animationEnabled = true, reduceMotion = true)
        )
    }
}