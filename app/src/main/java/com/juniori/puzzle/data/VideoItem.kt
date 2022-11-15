package com.juniori.puzzle.data

import com.google.gson.annotations.SerializedName

data class VideoItem(
    @SerializedName("name") val videoName: String,
    @SerializedName("fields") val videoDetail: VideoDetail,
    @SerializedName("createTime") val createTime: String? = null,
    @SerializedName("updateTime") val updateTime: String? = null
)
