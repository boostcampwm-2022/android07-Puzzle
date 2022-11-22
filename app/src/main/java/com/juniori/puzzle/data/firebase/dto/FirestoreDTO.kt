package com.juniori.puzzle.data.firebase.dto

import com.google.gson.annotations.SerializedName

data class RunQueryRequestDTO(
    val structuredQuery: StructuredQuery
)

data class RunQueryResponseDTO(
    @SerializedName("document") val videoItem: VideoItem,
    @SerializedName("readTime") val readTime: String
)
