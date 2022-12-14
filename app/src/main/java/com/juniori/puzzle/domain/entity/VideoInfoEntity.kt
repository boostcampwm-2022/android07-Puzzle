package com.juniori.puzzle.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoInfoEntity(
    val documentId: String,
    val ownerUid: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val isPrivate: Boolean,
    val likedCount: Int,
    val likedUserUidList: List<String>,
    val updateTime: Long,
    val location: String,
    val locationKeyword: List<String>,
    val memo: String
) : Parcelable
