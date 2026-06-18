package com.android.purebilibili.feature.home.components.cards

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoCardImageDecodeStrategyStructureTest {

    @Test
    fun `video cards let Coil resolve decode size from layout constraints`() {
        val sourceRoot = File("src/main/java/com/android/purebilibili/feature/home/components/cards")
        val cardSources = listOf(
            "VideoCard.kt",
            "StoryVideoCard.kt",
            "GlassVideoCard.kt",
            "CinematicVideoCard.kt"
        ).associateWith { fileName -> sourceRoot.resolve(fileName).readText() }

        cardSources.forEach { (fileName, source) ->
            val imageRequests = Regex(
                "ImageRequest\\.Builder\\([^)]*\\)[\\s\\S]*?\\.build\\(\\)"
            ).findAll(source)
                .map { it.value }
                .filter { request -> request.contains(".data(coverUrl)") }
                .toList()
            assertTrue(imageRequests.isNotEmpty(), "$fileName 应包含封面图片请求")
            assertFalse(
                imageRequests.any { request -> request.contains(".size(") },
                "$fileName 不应覆盖 Coil 根据布局约束推导的解码尺寸"
            )
        }
    }
}
