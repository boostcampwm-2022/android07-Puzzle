package com.juniori.puzzle.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.usecase.RequestLogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val requestLogoutUseCase: RequestLogoutUseCase
) : ViewModel() {
    private val _navigateToIntroPageEvent = MutableLiveData<Resource<Unit>>(Resource.Loading)
    val navigateToIntroPageEvent: LiveData<Resource<Unit>> = _navigateToIntroPageEvent

    fun requestLogout() {
        _navigateToIntroPageEvent.value = requestLogoutUseCase()
    }
}
