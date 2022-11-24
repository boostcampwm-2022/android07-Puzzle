package com.juniori.puzzle.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity

interface AuthRepository {
    fun getCurrentUserInfo(): Resource<UserInfoEntity>
    suspend fun requestLogin(acct: GoogleSignInAccount): Resource<UserInfoEntity>
    fun requestLogout(): Resource<Unit>
    fun requestWithdraw(): Resource<Unit>
    suspend fun updateNickname(newNickname: String): Resource<Unit>
}
