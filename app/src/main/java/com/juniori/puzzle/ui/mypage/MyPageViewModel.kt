package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.SingleLiveEvent
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.data.firebase.FirestoreDataSource
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.RequestLogoutUseCase
import com.juniori.puzzle.domain.usecase.RequestWithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val requestLogoutUseCase: RequestLogoutUseCase,
    private val requestWithdrawUseCase: RequestWithdrawUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val firestoreDataSource: FirestoreDataSource
) : ViewModel() {
    private val _navigateToIntroPageEvent = MutableLiveData<Resource<Unit>>(Resource.Loading)
    val navigateToIntroPageEvent: LiveData<Resource<Unit>> = _navigateToIntroPageEvent

    private val _finishApplicationEvent = MutableLiveData<Resource<Unit>>()
    val finishApplicationEvent: LiveData<Resource<Unit>> = _finishApplicationEvent

    private val _navigateToUpdateNicknamePageEvent = SingleLiveEvent<Unit>()
    val navigateToUpdateNicknamePageEvent: SingleLiveEvent<Unit> = _navigateToUpdateNicknamePageEvent

    val userNickname = MutableLiveData<String>()

    init {
        val data = getUserInfoUseCase()

        if (data is Resource.Success) {
            userNickname.value = data.result.nickname
        } else {
            userNickname.value = "누구세요"
        }
    }

    fun updateUserInfo(newNickname: String) {
        viewModelScope.launch {
            with(getUserInfoUseCase()) {
                if (this is Resource.Success) {
                    firestoreDataSource.changeUserNickname(
                        this.result.uid,
                        newNickname,
                        this.result.profileImage
                    )
                }
            }
        }
    }

    fun requestLogout() {
        _navigateToIntroPageEvent.value = requestLogoutUseCase()
    }

    fun requestWithdraw() {
        _finishApplicationEvent.value = requestWithdrawUseCase()
    }

    fun navigateToUpdateNicknamePage() {
        _navigateToUpdateNicknamePageEvent.call()
    }

    fun updateUserNickname(newNickname: String?) {
        userNickname.value = newNickname ?: "누구세요"
    }
}
