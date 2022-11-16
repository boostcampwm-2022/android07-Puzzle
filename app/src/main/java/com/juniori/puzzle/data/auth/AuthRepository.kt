package com.juniori.puzzle.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.juniori.puzzle.data.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(acct: GoogleSignInAccount): Resource<FirebaseUser>
    fun logout()
}
