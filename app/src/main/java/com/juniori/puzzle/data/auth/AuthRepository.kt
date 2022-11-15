package com.juniori.puzzle.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.juniori.puzzle.data.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(credential: AuthCredential): Resource<FirebaseUser>
    fun logout()
}
