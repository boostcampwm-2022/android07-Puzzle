package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juniori.puzzle.SingleLiveEvent
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLogoutUseCase
import com.juniori.puzzle.domain.usecase.RequestWithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestWithdrawUseCase: RequestWithdrawUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    val makeLogoutDialogEvent = SingleLiveEvent<Unit>()
    val makeWithdrawDialogEvent = SingleLiveEvent<Unit>()
    val navigateToUpdateNicknamePageEvent = SingleLiveEvent<Unit>()
    val userNickname = MutableLiveData<String?>()

    init {
        val data = getUserInfoUseCase()

        if (data is Resource.Success) {
            userNickname.value = data.result.nickname
        }
        else {
            userNickname.value = null
        }
    }

    fun makeLogoutDialog() {
        makeLogoutDialogEvent.call()
    }

    fun makeWithdrawDialog() {
        makeWithdrawDialogEvent.call()
    }

    fun requestLogout(): Resource<Unit> {
        return requestLogoutUseCase()
    }

    fun requestWithdraw(): Resource<Unit> {
        return requestWithdrawUseCase()
    }

    fun navigateToUpdateNicknamePage() {
        navigateToUpdateNicknamePageEvent.call()
    }
}
