package com.android.purebilibili.feature.settings

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsRootCategoryContentStructureTest {

    @Test
    fun rootCategoryContent_usesStateAndActionHolders() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        assertTrue(source.contains("internal data class SettingsRootCategoryActions("))
        assertTrue(source.contains("internal data class SettingsRootCategoryState("))
        assertTrue(
            source.contains(
                """
                internal fun SettingsRootCategoryContent(
                    category: SettingsRootCategory,
                    actions: SettingsRootCategoryActions,
                    state: SettingsRootCategoryState
                )
                """.trimIndent()
            )
        )
    }

    @Test
    fun detailEntrySection_submitsDetailFocusBeforeOpeningEntry() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val sectionBlock = source
            .substringAfter("internal fun SettingsDetailEntrySection(")
            .substringBefore("internal fun SettingsRootCategoryContent(")

        assertTrue(sectionBlock.contains("resolveSettingsSceneDetailFocus(entry.target)?.let"))
        assertTrue(sectionBlock.contains("SettingsSearchFocusController.submit(detailFocus.target, detailFocus.focusId)"))
        assertTrue(sectionBlock.contains("entry.onClick()"))
        assertTrue(sectionBlock.contains("subtitle = entry.value"))
    }

    @Test
    fun feedSwitchDescription_allowsWrappingInIosSettings() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val feedSwitchBlock = source
            .substringAfter("private fun FeedSwitchItem(")
            .substringBefore("@Composable\nprivate fun FeedRefreshCountItem(")

        assertTrue(feedSwitchBlock.contains("text = subtitle"))
        assertTrue(!feedSwitchBlock.contains("maxLines = 1"))
    }

    @Test
    fun mobileSettingsRoot_usesNagramStyleHomeSections() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/SettingsScreen.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/SettingsScreen.kt")
        ).first { it.exists() }.readText()

        assertTrue(source.contains("SettingsSearchBarSection("))
        assertTrue(source.contains("SettingsRootCategoryListSection("))
        assertTrue(source.contains("SettingsAboutHomeSection("))
        assertTrue(source.contains("SettingsBackupHomeSection("))
        assertTrue(source.indexOf("SettingsRootCategoryListSection(") < source.indexOf("SettingsAboutHomeSection("))
        assertTrue(source.indexOf("SettingsAboutHomeSection(") < source.indexOf("SettingsBackupHomeSection("))
        assertFalse(source.contains("SupportAuthorCompactSection("))
    }

    @Test
    fun rootCategoryContent_usesStableDetailGroupsWithoutSceneShortcutRows() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val contentBlock = source
            .substringAfter("internal fun SettingsRootCategoryContent(")
            .substringBefore("@Composable\nfun SupportToolsSection(")

        assertTrue(contentBlock.contains("Column {\n        when (category)"))
        assertTrue(contentBlock.contains("SettingsDetailGroup("))
        assertTrue(contentBlock.contains("SettingsDetailEntrySection("))
        assertFalse(contentBlock.contains("SettingsSceneShortcutSection("))
        assertTrue(contentBlock.contains("SettingsRootCategory.CONTENT_PLAYBACK -> {"))
        assertTrue(contentBlock.contains("SettingsRootCategory.PRIVACY_STORAGE -> {"))
        assertTrue(contentBlock.contains("SettingsRootCategory.SYSTEM_ABOUT -> {"))
    }

    @Test
    fun mobileSettingsRootPinsSearchAboveNagramSections() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/SettingsScreen.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/SettingsScreen.kt")
        ).first { it.exists() }.readText()

        assertTrue(source.contains("SettingsSearchBarSection("))
        assertTrue(source.contains("activeRootCategoryName"))
        assertTrue(source.indexOf("SettingsSearchBarSection(") < source.indexOf("SettingsRootCategoryListSection("))
        assertFalse(source.contains("FollowAuthorSection("))
    }

    @Test
    fun mobileSettingsRootUsesCategoryRowsWithoutAccordionExpansion() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val sectionBlock = source
            .substringAfter("internal fun SettingsRootCategoryListSection(")
            .substringBefore("@Composable\ninternal fun SettingsDetailGroup(")

        assertTrue(sectionBlock.contains("title = category.title"))
        assertTrue(sectionBlock.contains("subtitle = category.subtitle"))
        assertTrue(sectionBlock.contains("onCategoryClick(category)"))
        assertFalse(sectionBlock.contains("AnimatedVisibility("), "Nagram-style root should navigate, not accordion-expand")
        assertFalse(sectionBlock.contains("SettingsRootCategoryContent("), "root rows should not inline detail content")
    }

    @Test
    fun mobileSettingsRootUsesCategoryDetailScreenInsteadOfInlineExpansion() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/SettingsScreen.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/SettingsScreen.kt")
        ).first { it.exists() }.readText()

        assertTrue(source.contains("activeRootCategoryName"))
        assertTrue(source.contains("SettingsRootCategoryContent("))
        assertTrue(source.contains("AnimatedContent("))
        assertTrue(source.contains("resolveSettingsRootCategoryContentTransform("))
        assertTrue(source.contains("label = \"SettingsRootBody\""))
        assertFalse(source.contains("isExpanded ="), "should not use accordion isExpanded")
        assertFalse(source.contains("onToggle ="), "should not use accordion onToggle")
        assertFalse(source.contains("expandedRootCategoryNames"))
    }

    @Test
    fun tabletSettingsRootUsesCompactSupportInMasterAndKeepsDetailFocused() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/TabletSettingsLayout.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/TabletSettingsLayout.kt")
        ).first { it.exists() }.readText()

        val masterBlock = source
            .substringAfter("// Master List")
            .substringBefore("secondaryContent =")
        val rootDetailBlock = source
            .substringAfter("// Category Root")
            .substringBefore("Spacer(modifier = Modifier\n                                .windowInsetsBottomHeight")

        assertTrue(masterBlock.contains("SettingsSearchBarSection("))
        assertTrue(masterBlock.contains("SupportAuthorCompactSection("))
        assertTrue(masterBlock.indexOf("SettingsSearchBarSection(") < masterBlock.indexOf("SupportAuthorCompactSection("))
        assertFalse(rootDetailBlock.contains("FollowAuthorSection("))
        assertTrue(rootDetailBlock.contains("SettingsRootCategoryContent("))
    }

    @Test
    fun aboutSupport_keepsReleaseChannelBelowAboutDetailsWithoutDuplicateAuthorCard() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val aboutBlock = source
            .substringAfter("SettingsRootCategory.SYSTEM_ABOUT -> {")
            .substringBefore("SupportToolsSection(")

        assertTrue(aboutBlock.indexOf("AboutSection(") < aboutBlock.indexOf("ReleaseChannelPinnedCard("))
        assertFalse(aboutBlock.contains("FollowAuthorSection("))
    }

    @Test
    fun aboutSectionShowsProjectOverviewAndStaticContributorsBeforeRows() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val aboutSectionBlock = source
            .substringAfter("fun AboutSection(")
            .substringBefore("@Composable\nprivate fun AboutProjectOverviewCard(")

        assertTrue(aboutSectionBlock.contains("AboutProjectOverviewCard(versionName = versionName)"))
        assertTrue(aboutSectionBlock.indexOf("AboutProjectOverviewCard(") < aboutSectionBlock.indexOf("SettingsCardGroup {"))
        assertTrue(source.contains("internal val AboutContributors = listOf("))
        assertTrue(source.contains("AboutContributor(\"jay3-yy\", \"jay3-yy\")"))
        assertTrue(source.contains("AboutContributor(\"Chenx Dust\""))
        assertTrue(source.contains("AboutContributor(\"usontong\""))
        assertTrue(source.contains("AboutContributor(\"Leko\", \"lekoOwO\")"))
    }

    @Test
    fun aboutContributors_useGithubProfilesAndClickableAvatars() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val contributorBlock = source
            .substringAfter("internal data class AboutContributor(")
            .substringBefore("// ponytail: 静态列表避免关于页每次打开都请求 GitHub")
        val overviewBlock = source
            .substringAfter("private fun AboutProjectOverviewCard(")
            .substringBefore("@Composable\nprivate fun AboutContributorItem(")
        val itemBlock = source
            .substringAfter("private fun AboutContributorItem(")

        assertTrue(contributorBlock.contains("val githubLogin: String"))
        assertTrue(contributorBlock.contains("\"https://github.com/${'$'}githubLogin\""))
        assertTrue(contributorBlock.contains("\"${'$'}profileUrl.png?size=160\""))
        assertTrue(overviewBlock.contains("text = \"贡献者\""))
        assertFalse(overviewBlock.contains("其他贡献者"))
        assertFalse(overviewBlock.contains("顺手"))
        assertTrue(overviewBlock.contains("remember { AboutSlogans.random() }"))
        assertTrue(source.contains("删繁留简见初心"))
        assertTrue(source.contains("广告退场方显净"))
        assertTrue(source.contains("弱水三千凡君取"))
        assertFalse(source.contains("主题:"))
        assertTrue(overviewBlock.contains("FlowRow("))
        assertFalse(overviewBlock.contains("horizontalScroll("))
        assertTrue(itemBlock.contains("LocalUriHandler.current"))
        assertTrue(itemBlock.contains("uriHandler.openUri(contributor.profileUrl)"))
        assertTrue(itemBlock.contains("Box("))
        assertTrue(itemBlock.contains("contentScale = ContentScale.Crop"))
    }

    @Test
    fun settingsSubpages_useNagramStyleSectionNames() {
        val settingsSections = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()
        val appearance = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/AppearanceSettingsScreen.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/AppearanceSettingsScreen.kt")
        ).first { it.exists() }.readText()
        val playback = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/PlaybackSettingsScreen.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/PlaybackSettingsScreen.kt")
        ).first { it.exists() }.readText()
        val animation = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/screen/AnimationSettingsScreen.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/screen/AnimationSettingsScreen.kt")
        ).first { it.exists() }.readText()

        assertTrue(settingsSections.contains("SettingsDetailGroup(title = \"显示与交互\")"))
        assertTrue(settingsSections.contains("SettingsDetailGroup(title = \"画质与播放\")"))
        assertTrue(settingsSections.contains("Spacer(modifier = Modifier.height(12.dp))\n                SettingsDetailGroup(title = \"隐私与安全\")"))
        assertTrue(appearance.contains("IOSSectionTitle(\"显示模式\")"))
        assertTrue(appearance.contains("IOSSectionTitle(\"字体与密度\")"))
        assertTrue(appearance.contains("IOSSectionTitle(\"开屏与图标\")"))
        assertTrue(playback.contains("IOSSectionTitle(\"小窗与后台\")"))
        assertTrue(playback.contains("IOSSectionTitle(\"诊断\")"))
        assertTrue(playback.indexOf("IOSSectionTitle(\"网络与画质\")") < playback.indexOf("IOSSectionTitle(\"互动与评论\")"))
        assertTrue(animation.contains("IOSSectionTitle(\"玻璃效果\")"))
        assertTrue(animation.contains("IOSSectionTitle(\"底栏入口\")"))
    }

    @Test
    fun aboutProjectOverview_usesRasterLauncherAssetForComposePainter() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val overviewBlock = source
            .substringAfter("private fun AboutProjectOverviewCard(")
            .substringBefore("@Composable\nprivate fun ContributorAvatarRow(")

        assertFalse(overviewBlock.contains("R.mipmap.ic_launcher_bilipai)"))
        assertTrue(overviewBlock.contains("R.mipmap.ic_launcher_bilipai_foreground"))
    }

    @Test
    fun aboutSection_doesNotRenderDuplicateReleaseChannelDisclaimerEntry() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val aboutSectionBlock = source
            .substringAfter("fun AboutSection(")
            .substringBefore("@Composable\nfun CheckUpdateSection(")

        assertFalse(aboutSectionBlock.contains("title = \"发布渠道声明\""))
        assertFalse(aboutSectionBlock.contains("SettingsSearchTarget.DISCLAIMER"))
    }

    @Test
    fun releaseChannelPinnedCard_keepsActionsInOneLine() {
        val source = listOf(
            File("app/src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt"),
            File("src/main/java/com/android/purebilibili/feature/settings/ui/SettingsSections.kt")
        ).first { it.exists() }.readText()

        val pinnedCardBlock = source
            .substringAfter("fun ReleaseChannelPinnedCard(")
            .substringBefore("@Composable\nfun SettingsSubpageEntrySection(")

        assertTrue(pinnedCardBlock.contains("modifier = Modifier.fillMaxWidth()"))
        assertTrue(pinnedCardBlock.contains("modifier = Modifier.weight(1f)"))
        assertTrue(pinnedCardBlock.contains("softWrap = false"))
        assertTrue(pinnedCardBlock.contains("maxLines = 1"))
    }
}
