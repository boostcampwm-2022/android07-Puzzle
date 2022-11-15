package com.juniori.puzzle.data

import com.google.gson.annotations.SerializedName

data class FireStoreResponse(
    @SerializedName("documents") val videoItems: List<VideoItem>
)