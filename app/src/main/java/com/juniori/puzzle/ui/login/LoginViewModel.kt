package com.juniori.puzzle.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLoginUseCase
import com.juniori.puzzle.domain.usecase.RequestLogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    getUserInfoUseCase: GetUserInfoUseCase,
    private val requestLoginUseCase: RequestLoginUseCase,
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel() {
    private val _loginFlow = MutableStateFlow<Resource<UserInfoEntity>?>(null)
    val loginFlow: StateFlow<Resource<UserInfoEntity>?> = _loginFlow

    init {
        getUserInfoUseCase().let { currentUser ->
            _loginFlow.value = currentUser
        }
    }

    fun loginUser(account: GoogleSignInAccount) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = requestLoginUseCase(account).apply {
            if (this is Resource.Success) {
                firestoreDataSource.postUserItem(
                    this.result.uid,
                    this.result.nickname,
                    this.result.profileImage
                )
            }
        }
        _loginFlow.value = result
    }

    private fun logout() {
        val logoutResult = requestLogoutUseCase()

        if (logoutResult is Resource.Success) {
            _loginFlow.value = null
        } else {
            throw Exception()
        }
    }
}