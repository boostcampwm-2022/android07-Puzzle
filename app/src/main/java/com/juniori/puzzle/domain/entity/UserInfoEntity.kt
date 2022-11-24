package com.juniori.puzzle.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoEntity(
    val uid: String,
    val nickname: String,
    val profileImage: String,
    val videoList: List<VideoInfoEntity>? = null
) : Parcelable
