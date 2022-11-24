package com.juniori.puzzle.data.firebase.dto

import com.google.gson.annotations.SerializedName
import com.juniori.puzzle.domain.entity.VideoInfoEntity

data class RunQueryRequestDTO(
    val structuredQuery: StructuredQuery
)

data class RunQueryResponseDTO(
    @SerializedName("document") val videoItem: VideoItem?,
    @SerializedName("readTime") val readTime: String
)

fun List<RunQueryResponseDTO>.getVideoInfoEntity(): List<VideoInfoEntity> =
    filter { it.videoItem != null }.map {
        it.videoItem?.getVideoInfoEntity()
            ?: throw Exception("getVideoItem from ResponseDTO Failed")
    }