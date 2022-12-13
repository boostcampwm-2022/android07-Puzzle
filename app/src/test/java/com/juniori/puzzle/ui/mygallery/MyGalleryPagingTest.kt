package com.juniori.puzzle.ui.mygallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.juniori.puzzle.data.Resource
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.usecase.GetMyVideoListUseCase
import com.juniori.puzzle.domain.usecase.GetSearchedMyVideoUseCase
import com.juniori.puzzle.domain.usecase.GetUserInfoUseCase
import com.juniori.puzzle.mock.getVideoListMockData
import com.juniori.puzzle.util.getOrAwaitValue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MyGalleryPagingTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val mockUserEntity = UserInfoEntity("aaa", "nickname", "profile")
    private val firstVideoList = getVideoListMockData().map { it.copy(ownerUid = "aaa") }
    private val extraList = getVideoListMockData().map { it.copy(documentId = "NewList", ownerUid = "aaa") }.subList(0, 6)

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
    fun endPagingTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(emptyList()))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        mockMyGalleryViewModel.getPaging(1)

        Assert.assertEquals(firstVideoList, mockMyGalleryViewModel.list.getOrAwaitValue())
    }

    @Test
    fun singlePagingTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        mockMyGalleryViewModel.getPaging(1)

        Assert.assertEquals(firstVideoList + extraList, mockMyGalleryViewModel.list.getOrAwaitValue())
    }

    @Test
    fun multiPagingTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 4)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 5)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(3)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(4)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(5)
        delay(PAGING_DELAY)

        Assert.assertEquals(66, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun shortPagingDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(Resource.Success(firstVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(3)
        delay(PAGING_DELAY)

        Assert.assertEquals(36, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun shortFirstDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(Resource.Success(firstVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(3)
        delay(PAGING_DELAY)

        Assert.assertEquals(36, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun pagingWithFailureTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1))
            .thenReturn(Resource.Failure(Exception()))
            .thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(firstVideoList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)

        Assert.assertEquals(30, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun pagingWithFailureEmptyDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1))
            .thenReturn(Resource.Success(emptyList()))
            .thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)

        Assert.assertEquals(24, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun emptyPagingDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(emptyList()))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)

        Assert.assertEquals(18, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun pagingSkippingNumberTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 3)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 4)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(4)
        delay(PAGING_DELAY)

        Assert.assertEquals(24, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun pagingWithFirstEmptyDataTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0)).thenReturn(Resource.Success(emptyList()))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)

        Assert.assertEquals(18, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @Test
    fun pagingWithFirstFailureTest(): Unit = runBlocking {
        Mockito.`when`(mockGetUserInfoUseCase()).thenReturn(Resource.Success(mockUserEntity))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 0))
            .thenReturn(Resource.Failure(Exception()))
            .thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 1)).thenReturn(Resource.Success(firstVideoList))
        Mockito.`when`(mockGetMyVideoListUseCase("aaa", 2)).thenReturn(Resource.Success(extraList))

        mockMyGalleryViewModel.setQueryText(null)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getMyData()
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(1)
        delay(PAGING_DELAY)
        mockMyGalleryViewModel.getPaging(2)
        delay(PAGING_DELAY)

        Assert.assertEquals(30, mockMyGalleryViewModel.list.getOrAwaitValue().size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    companion object {
        const val PAGING_DELAY = 500L
    }
}