package com.juniori.puzzle.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val requestLoginUseCase: RequestLoginUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
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
        val result = requestLoginUseCase(account)
        _loginFlow.value = result
    }
}