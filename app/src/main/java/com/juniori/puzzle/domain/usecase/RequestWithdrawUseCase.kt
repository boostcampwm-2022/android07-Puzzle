package com.juniori.puzzle.domain.usecase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.domain.repository.AuthRepository
import javax.inject.Inject

class RequestWithdrawUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(acct: GoogleSignInAccount) = authRepository.requestWithdraw(acct)
}