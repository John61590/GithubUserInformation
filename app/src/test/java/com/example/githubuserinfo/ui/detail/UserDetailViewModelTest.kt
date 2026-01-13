package com.example.githubuserinfo.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.githubuserinfo.data.GithubRepository
import com.example.githubuserinfo.data.model.GithubUserDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: GithubRepository
    private lateinit var viewModel: UserDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = UserDetailViewModel(repository)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser should emit Loading then Success when repository returns user detail`() = runTest {
        // Given
        val login = "octocat"
        val mockUserDetail = GithubUserDetail(
            login = login,
            id = 1L,
            name = "The Octocat",
            bio = "GitHub's mascot",
            avatarUrl = "https://github.com/images/error/octocat_happy.gif",
            htmlUrl = "https://github.com/octocat",
            followers = 100,
            following = 50,
            publicRepos = 10,
            updatedAt = "2024-01-01T00:00:00Z"
        )
        whenever(repository.getUserDetail(login)).thenReturn(mockUserDetail)

        // When
        viewModel.loadUser(login)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is UserDetailState.Success)
        assertEquals(mockUserDetail, (state as UserDetailState.Success).detail)
        verify(repository).getUserDetail(login)
    }

    @Test
    fun `loadUser should emit Loading then Error when repository throws exception`() = runTest {
        // Given
        val login = "nonexistent"
        val errorMessage = "User not found"
        whenever(repository.getUserDetail(login)).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.loadUser(login)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is UserDetailState.Error)
        assertEquals(errorMessage, (state as UserDetailState.Error).message)
        verify(repository).getUserDetail(login)
    }

    @Test
    fun `loadUser should set Loading state initially`() = runTest {
        // Given
        val login = "octocat"
        val mockUserDetail = GithubUserDetail(
            login = login,
            id = 1L,
            name = null,
            bio = null,
            avatarUrl = null,
            htmlUrl = null,
            followers = 0,
            following = 0,
            publicRepos = 0,
            updatedAt = null
        )
        whenever(repository.getUserDetail(login)).thenReturn(mockUserDetail)

        // When
        viewModel.loadUser(login)
        // Check state before coroutine completes
        val loadingState = viewModel.state.value
        assertTrue(loadingState is UserDetailState.Loading)

        advanceUntilIdle()

        // Then - should transition to Success
        val finalState = viewModel.state.value
        assertTrue(finalState is UserDetailState.Success)
    }

    @Test
    fun `loadUser should handle user with null optional fields`() = runTest {
        // Given
        val login = "user"
        val mockUserDetail = GithubUserDetail(
            login = login,
            id = 1L,
            name = null,
            bio = null,
            avatarUrl = null,
            htmlUrl = null,
            followers = 0,
            following = 0,
            publicRepos = 0,
            updatedAt = null
        )
        whenever(repository.getUserDetail(login)).thenReturn(mockUserDetail)

        // When
        viewModel.loadUser(login)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is UserDetailState.Success)
        val detail = (state as UserDetailState.Success).detail
        assertEquals(login, detail.login)
        assertEquals(null, detail.name)
        assertEquals(null, detail.bio)
    }

    @Test
    fun `loadUser should handle network error`() = runTest {
        // Given
        val login = "user"
        val errorMessage = "No internet connection"
        whenever(repository.getUserDetail(login)).thenThrow(
            RuntimeException(errorMessage)
        )

        // When
        viewModel.loadUser(login)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is UserDetailState.Error)
        assertEquals(errorMessage, (state as UserDetailState.Error).message)
    }

    @Test
    fun `loadUser should emit Loading immediately`() = runTest {
        // Given
        val login = "octocat"
        val mockUserDetail = GithubUserDetail(
            login = login,
            id = 1L,
            name = "The Octocat",
            bio = null,
            avatarUrl = null,
            htmlUrl = null,
            followers = 0,
            following = 0,
            publicRepos = 0,
            updatedAt = null
        )
        whenever(repository.getUserDetail(login)).thenReturn(mockUserDetail)

        // When
        viewModel.loadUser(login)

        // Then - should emit Loading immediately before coroutine executes
        val initialState = viewModel.state.value
        assertTrue(initialState is UserDetailState.Loading)

        // Advance to get final state
        advanceUntilIdle()
        val finalState = viewModel.state.value
        assertTrue(finalState is UserDetailState.Success)
    }

    @Test
    fun `loadUser should handle multiple sequential loads`() = runTest {
        // Given
        val user1 = GithubUserDetail(
            login = "user1",
            id = 1L,
            name = "User 1",
            bio = null,
            avatarUrl = null,
            htmlUrl = null,
            followers = 10,
            following = 5,
            publicRepos = 3,
            updatedAt = null
        )
        val user2 = GithubUserDetail(
            login = "user2",
            id = 2L,
            name = "User 2",
            bio = null,
            avatarUrl = null,
            htmlUrl = null,
            followers = 20,
            following = 10,
            publicRepos = 5,
            updatedAt = null
        )
        whenever(repository.getUserDetail("user1")).thenReturn(user1)
        whenever(repository.getUserDetail("user2")).thenReturn(user2)

        // When - load first user
        viewModel.loadUser("user1")
        advanceUntilIdle()

        var state = viewModel.state.value
        assertTrue(state is UserDetailState.Success)
        assertEquals("User 1", (state as UserDetailState.Success).detail.name)

        // Then - load second user
        viewModel.loadUser("user2")
        assertTrue(viewModel.state.value is UserDetailState.Loading)

        advanceUntilIdle()
        state = viewModel.state.value
        assertTrue(state is UserDetailState.Success)
        assertEquals("User 2", (state as UserDetailState.Success).detail.name)
    }

    @Test
    fun `state should be accessible and correct`() = runTest {
        // Given
        val login = "octocat"
        val mockUserDetail = GithubUserDetail(
            login = login,
            id = 1L,
            name = "The Octocat",
            bio = "GitHub's mascot",
            avatarUrl = "https://github.com/images/error/octocat_happy.gif",
            htmlUrl = "https://github.com/octocat",
            followers = 100,
            following = 50,
            publicRepos = 10,
            updatedAt = "2024-01-01T00:00:00Z"
        )
        whenever(repository.getUserDetail(login)).thenReturn(mockUserDetail)

        // When
        viewModel.loadUser(login)
        advanceUntilIdle()

        // Then - state should be observable and correct
        val state = viewModel.state.value
        assertTrue(state is UserDetailState.Success)
        val detail = (state as UserDetailState.Success).detail
        assertEquals(login, detail.login)
        assertEquals(100, detail.followers)
        assertEquals(50, detail.following)
    }
}
