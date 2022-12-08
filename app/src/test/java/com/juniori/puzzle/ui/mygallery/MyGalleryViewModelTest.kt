package com.juniori.puzzle.ui.mygallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.VideoInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.mock.getVideoListMockData
import com.juniori.puzzle.util.GalleryState
import com.juniori.puzzle.util.getOrAwaitValue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MyGalleryViewModelTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val mockUserEntity = UserInfoEntity("aaa", "nickname", "profile")
    private val mockVideoList = getVideoListMockData().map { it.copy(ownerUid = "aaa") }

    lateinit var mockGetMyVideoListUseCase: GetMyVideoListUseCase
    lateinit var mockGetUserInfoUseCase: GetUserInfoUseCase
    lateinit var mockGetSearchedMyVideoUseCase: GetSearchedMyVideoUseCase
    lateinit var mockMyGalleryViewModel: MyGalleryViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        mockGetMyVideoListUseCase = Mockito.mock(GetMyVideoListUseCase::class.java)
        mockGetUserInfoUseCase = Mockito.mock(GetUserInfoUseCase::class.java)
        mockGetSearchedMyVideoUseCase = Mockito.mock(GetSearchedMyVideoUseCase::class.java)

        mockMyGalleryViewModel = MyGalleryViewModel(mockGetMyVideoListUseCase, mockGetUserInfoUseCase, mockGetSearchedMyVideoUseCase)
    }

    @Test
    fun getNormalFirstDataWithoutQueryTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(mockVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()

        assertEquals(mockVideoList, mockMyGalleryViewModel.list.getOrAwaitValue())
        println(mockMyGalleryViewModel.state.value)
    }

    @Test
    fun getNormalFirstDataWithQueryTest(): Unit = runBlocking {
        val mockVideoList = mockVideoList.filter { it.location.contains("서대문구") }

        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(Resource.Success(mockVideoList))

        mockMyGalleryViewModel.setQueryText("서대문구")
        mockMyGalleryViewModel.getMyData()

        assertEquals(mockVideoList, mockMyGalleryViewModel.state.getOrAwaitValue())
    }

    @Test
    fun getFirstDataWithoutUidTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Failure(Exception()))
        Mockito.`when`(mockGetSearchedMyVideoUseCase("aaa", 0, "서대문구")).thenReturn(Resource.Success(mockVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()

        assertEquals(GalleryState.NETWORK_ERROR_BASE, mockMyGalleryViewModel.state.getOrAwaitValue())
    }

    @Test
    fun emptyDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(emptyList()))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()

        assertEquals(emptyList<VideoInfoEntity>(), mockMyGalleryViewModel.list.getOrAwaitValue())
    }

    @Test
    fun failureDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Failure(Exception()))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()

        assertEquals(GalleryState.NETWORK_ERROR_BASE, mockMyGalleryViewModel.state.getOrAwaitValue())
    }

    @Test
    fun endPagingTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(mockVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(emptyList()))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        mockMyGalleryViewModel.getPaging(1)

        assertEquals(GalleryState.END_PAGING, mockMyGalleryViewModel.state.getOrAwaitValue())
    }

    @Test
    fun normalPagingTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(mockVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(mockVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        mockMyGalleryViewModel.getPaging(1)

        assertEquals(mockVideoList + mockVideoList, mockMyGalleryViewModel.list.getOrAwaitValue())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}