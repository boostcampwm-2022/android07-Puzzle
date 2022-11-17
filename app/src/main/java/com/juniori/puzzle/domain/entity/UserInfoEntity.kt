package com.juniori.puzzle.domain.entity

data class UserInfoEntity(
    val uid: String,
    val nickname: String,
    val profileImage: String,
    val videoList: List<VideoInfoEntity>? = null
)