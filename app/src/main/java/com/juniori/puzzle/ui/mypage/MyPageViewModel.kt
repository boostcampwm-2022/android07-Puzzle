package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.SingleLiveEvent
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLogoutUseCase
import com.juniori.puzzle.domain.usecase.RequestWithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestWithdrawUseCase: RequestWithdrawUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _requestLogoutFlow = MutableSharedFlow<Resource<Unit>>()
    val requestLogoutFlow: SharedFlow<Resource<Unit>> = _requestLogoutFlow

    private val _requestWithdrawFlow = MutableSharedFlow<Resource<Unit>>()
    val requestWithdrawFlow: SharedFlow<Resource<Unit>> = _requestWithdrawFlow

    private val _userNickname = MutableStateFlow("")
    val userNickname: StateFlow<String> = _userNickname

    val makeLogoutDialogEvent = SingleLiveEvent<Unit>()
    val makeWithdrawDialogEvent = SingleLiveEvent<Unit>()
    val navigateToUpdateNicknamePageEvent = SingleLiveEvent<Unit>()

    init {
        updateUserInfo()
    }

    fun makeLogoutDialog() {
        makeLogoutDialogEvent.call()
    }

    fun makeWithdrawDialog() {
        makeWithdrawDialogEvent.call()
    }

    fun navigateToUpdateNicknamePage() {
        navigateToUpdateNicknamePageEvent.call()
    }

    fun requestLogout() {
        viewModelScope.launch {
            _requestLogoutFlow.emit(Resource.Loading)
            withContext(Dispatchers.IO) {
                _requestLogoutFlow.emit(requestLogoutUseCase())
            }
        }
    }

    fun requestWithdraw() {
        viewModelScope.launch {
            _requestWithdrawFlow.emit(Resource.Loading)
            withContext(Dispatchers.IO) {
                _requestWithdrawFlow.emit(requestWithdrawUseCase())
            }
        }
    }

    fun updateUserInfo() {
        viewModelScope.launch {
            val data = getUserInfoUseCase()

            if (data is Resource.Success) {
                _userNickname.value = data.result.nickname
            }
            else {
                _userNickname.value = ""
            }
        }
    }

    fun updateUserNickname(newNickname: String) {
        _userNickname.value = newNickname
    }
}
