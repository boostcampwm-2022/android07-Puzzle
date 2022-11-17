package com.juniori.puzzle.data.firebase.dto

import com.google.gson.annotations.SerializedName

data class VideoItem(
    @SerializedName("name") val videoName: String,
    @SerializedName("fields") val videoDetail: VideoDetail,
    @SerializedName("createTime") val createTime: String? = null,
    @SerializedName("updateTime") val updateTime: String? = null
)

data class VideoDetail(
    @SerializedName("owner_uid") val ownerUid: StringValue,
    @SerializedName("video_url") val videoUrl: StringValue,
    @SerializedName("thumb_url") val thumbUrl: StringValue,
    @SerializedName("is_private") val isPrivate: BooleanValue,
    @SerializedName("like_count") val likeCount: IntegerValue,
    @SerializedName("liked_user_list") val likedUserList: ArrayValue,
    @SerializedName("update_time") val updateTime: IntegerValue,
    @SerializedName("location") val location: StringValue,
    @SerializedName("memo") val memo: StringValue,
)
