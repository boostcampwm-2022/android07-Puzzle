package com.juniori.puzzle.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.util.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun updateNickname(newNickname: String): Resource<Unit> {
        val newProfile = UserProfileChangeRequest.Builder()
            .setDisplayName(newNickname)
            .build()

        val result = firebaseAuth.currentUser?.updateProfile(newProfile)
        return result?.let { task ->
            if (task.isSuccessful) {
                Resource.Success(Unit)
            }
            else {
                Resource.Failure(Exception())
            }
        } ?: kotlin.run { Resource.Failure(Exception()) }
    }

    override fun getCurrentUserInfo(): Resource<UserInfoEntity> {
        return firebaseAuth.currentUser?.let { firebaseUser ->
            Resource.Success(
                UserInfoEntity(
                    firebaseUser.uid,
                    firebaseUser.displayName ?: "",
                    firebaseUser.photoUrl?.toString() ?: ""
                )
            )
        } ?: kotlin.run { Resource.Failure(Exception()) }
    }

    override suspend fun requestLogin(acct: GoogleSignInAccount): Resource<UserInfoEntity> {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        return try {
            val result = firebaseAuth.signInWithCredential(credential).await()

            result.user?.let { firebaseUser ->
                Resource.Success(
                    UserInfoEntity(
                        firebaseUser.uid,
                        firebaseUser.displayName ?: "",
                        firebaseUser.photoUrl?.toString() ?: ""
                    )
                )
            }  ?: kotlin.run { Resource.Failure(Exception()) }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun requestLogout(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            Resource.Success(Unit)
        } catch (exception: Exception) {
            Resource.Failure(exception)
        }
    }

    override fun requestWithdraw(): Resource<Unit> {
        return try {
            firebaseAuth.currentUser?.delete() ?: throw java.lang.Exception()
            Resource.Success(Unit)
        }
        catch (e: java.lang.Exception) {
            Resource.Failure(Exception())
        }
    }
}
