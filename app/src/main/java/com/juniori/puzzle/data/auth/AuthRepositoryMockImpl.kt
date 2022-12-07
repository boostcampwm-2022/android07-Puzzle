package com.juniori.puzzle.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository

class AuthRepositoryMockImpl(private val status: Resource<UserInfoEntity>): AuthRepository {
    override fun getCurrentUserInfo(): Resource<UserInfoEntity> = status

    override suspend fun requestLogin(acct: GoogleSignInAccount) = status

    override suspend fun requestLogout(): Resource<Unit> {
        return if(status is Resource.Success) {
            Resource.Success(Unit)
        }
        else {
            Resource.Failure(Exception())
        }
    }

    override suspend fun requestWithdraw(): Resource<Unit> {
        return if(status is Resource.Success) {
            Resource.Success(Unit)
        }
        else {
            Resource.Failure(Exception())
        }
    }

    override suspend fun updateNickname(newNickname: String): Resource<Unit> {
        return if(status is Resource.Success) {
            Resource.Success(Unit)
        }
        else {
            Resource.Failure(Exception())
        }
    }
}