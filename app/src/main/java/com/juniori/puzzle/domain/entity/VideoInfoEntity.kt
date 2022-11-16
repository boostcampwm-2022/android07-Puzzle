package com.juniori.puzzle.domain.entity

data class VideoInfoEntity(
    val ownerUid: String,
    val videoName: String,
    val thumbnailImage: String,
    val isPrivate: Boolean,
    val likedUserUidList: List<String>,
    val updateTime: Long,
    val location: LocationInfoEntity,
    val memo: String
)
