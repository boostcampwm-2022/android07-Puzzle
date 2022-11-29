package com.juniori.puzzle.data.firebase.dto

import com.google.gson.annotations.SerializedName
import com.juniori.puzzle.domain.entity.UserInfoEntity

data class UserItem(
    @SerializedName("name") val uid: String,
    @SerializedName("fields") val userDetail: UserDetail,
    @SerializedName("createTime") val createTime: String? = null,
    @SerializedName("updateTime") val updateTime: String? = null
) {
    fun getUserInfoEntity(): UserInfoEntity {
        return userDetail.toUserInfoEntity(uid.substringAfter("userReal/"))
    }
}

data class UserDetail(
    @SerializedName("user_display_name") val nickname: StringValue,
    @SerializedName("profile_image") val profileImage: StringValue,
) {
    fun toUserInfoEntity(documentId: String): UserInfoEntity {
        return UserInfoEntity(
            documentId,
            nickname.stringValue,
            profileImage.stringValue,
            null
        )
    }
}
