package com.juniori.puzzle.data.firebase.dto

import com.juniori.puzzle.domain.entity.VideoInfoEntity
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class VideoItemConvertTest {
    lateinit var videoDetail: VideoDetail
    lateinit var videoItem: VideoItem
    lateinit var videoInfoEntity: VideoInfoEntity

    @Before
    fun setUp() {
        val listValue = StringValues(listOf(StringValue("aaa"), StringValue("bbb"), StringValue("ccc"), StringValue("ddd"), StringValue("eee")))

        videoDetail = VideoDetail(
            StringValue("Test_uid"),
            StringValue("Video_Url"),
            StringValue("Thumb_url"),
            BooleanValue(true),
            IntegerValue(5),
            ArrayValue(listValue),
            IntegerValue(987595293094203),
            StringValue("Location"),
            ArrayValue(listValue),
            StringValue("Golf")
        )
        videoItem = VideoItem("videoReal/VideoName", videoDetail)

        videoInfoEntity = VideoInfoEntity(
            "VideoName",
            "Test_uid",
            "Video_Url",
            "Thumb_url",
            true,
            5,
            listOf("aaa", "bbb", "ccc", "ddd", "eee"),
            987595293094203,
            "Location",
            listOf("aaa"),
            "Golf"
        )
    }

    @Test
    fun normalConvertTest() {
        val testVideoInfoEntity = videoItem.getVideoInfoEntity()

        assertEquals(videoInfoEntity, testVideoInfoEntity)
    }

    @Test
    fun emptyCommentConvertTest() {
        val testVideoDetail = videoDetail.copy(likeCount = IntegerValue(0), likedUserList = ArrayValue(StringValues(null)))
        val testVideoItem = videoItem.copy(videoDetail = testVideoDetail)

        val testVideoEntity = testVideoItem.getVideoInfoEntity()
        val targetVideoEntity = videoInfoEntity.copy(likedCount = 0, likedUserUidList = emptyList())

        assertEquals(targetVideoEntity, testVideoEntity)
    }
}