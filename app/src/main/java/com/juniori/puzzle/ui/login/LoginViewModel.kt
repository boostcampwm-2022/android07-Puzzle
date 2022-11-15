package com.juniori.puzzle.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    init {
        authRepository.currentUser?.let { currentUser ->
            _loginFlow.value = Resource.Success(currentUser)
        }
    }

    fun loginUser(credential: AuthCredential) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = authRepository.login(credential)
        _loginFlow.value = result
    }

    private fun logout() {
        authRepository.logout()
        _loginFlow.value = null
    }
}