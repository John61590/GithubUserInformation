package com.example.githubuserinfo.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.githubuserinfo.data.GithubRepository
import com.example.githubuserinfo.data.model.GithubUserSummary
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
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: GithubRepository
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadInitialUsers should emit Loading then Success when repository returns users`() = runTest {
        // Given
        val mockUsers = listOf(
            GithubUserSummary("user1", 1L, "avatar1", "User"),
            GithubUserSummary("user2", 2L, "avatar2", "User")
        )
        whenever(repository.getUsers(null, 50)).thenReturn(mockUsers)

        // When - viewModel is created with mock repository
        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // Then - verify final state is Success with correct users
        val state = viewModel.state.value
        assertTrue(state is UserListState.Success)
        assertEquals(mockUsers, (state as UserListState.Success).users)
        verify(repository).getUsers(null, 50)
    }

    @Test
    fun `loadInitialUsers should emit Loading then Error when repository throws exception`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(repository.getUsers(null, 50)).thenThrow(RuntimeException(errorMessage))

        // When - viewModel is created with mock repository that throws
        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // Then - verify final state is Error
        val state = viewModel.state.value
        assertTrue(state is UserListState.Error)
        assertEquals(errorMessage, (state as UserListState.Error).message)
    }

    @Test
    fun `loadInitialUsers should reset state to Loading`() = runTest {
        // Given
        val mockUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        whenever(repository.getUsers(null, 50)).thenReturn(mockUsers)

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // When - reload
        viewModel.loadInitialUsers()

        // Then - should be loading
        val state = viewModel.state.value
        assertTrue(state is UserListState.Loading)
    }

    @Test
    fun `loadInitialUsers should handle empty user list`() = runTest {
        // Given
        val emptyUsers = emptyList<GithubUserSummary>()
        whenever(repository.getUsers(null, 50)).thenReturn(emptyUsers)

        // When
        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // Then - should emit Success with empty list
        val state = viewModel.state.value
        assertTrue(state is UserListState.Success)
        assertEquals(emptyUsers, (state as UserListState.Success).users)
    }

    @Test
    fun `loadInitialUsers should emit Loading immediately on creation`() = runTest {
        // Given
        val mockUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        whenever(repository.getUsers(null, 50)).thenReturn(mockUsers)

        // When - just create viewModel, don't advance
        val viewModel = MainViewModel(repository)
        val initialState = viewModel.state.value

        // Then - state should be Loading immediately
        assertTrue(initialState is UserListState.Loading)

        // Advance to get the final state
        advanceUntilIdle()
        val finalState = viewModel.state.value
        assertTrue(finalState is UserListState.Success)
    }

    @Test
    fun `state should be mutable and update correctly`() = runTest {
        // Given
        val users = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        whenever(repository.getUsers(null, 50)).thenReturn(users)

        // When
        viewModel = MainViewModel(repository)
        val loadingState = viewModel.state.value

        // Then - initial state should be Loading
        assertTrue(loadingState is UserListState.Loading)

        // Wait for completion
        advanceUntilIdle()
        val successState = viewModel.state.value

        // Then - final state should be Success
        assertTrue(successState is UserListState.Success)
        assertEquals(users, (successState as UserListState.Success).users)
    }
}
