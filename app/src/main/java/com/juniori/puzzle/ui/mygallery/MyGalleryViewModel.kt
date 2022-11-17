package com.juniori.puzzle.ui.mygallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyGalleryViewModel @Inject constructor(
    private val getMyVideoListUseCase: GetMyVideoListUseCase,
    private val getSearchedMyVideoUseCase: GetSearchedMyVideoUseCase
) : ViewModel() {
    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

     init {
         viewModelScope.launch {
             val mockText = (getMyVideoListUseCase("aaa", 0) as Resource.Success).result[0].toString()
             _text.value = mockText
         }
     }
}
