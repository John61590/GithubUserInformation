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
import org.junit.Assert.assertFalse
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
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

    // Pagination tests

    @Test
    fun `loadMoreUsers should append new users to existing list`() = runTest {
        // Given
        val initialUsers = listOf(
            GithubUserSummary("user1", 1L, "avatar1", "User"),
            GithubUserSummary("user2", 2L, "avatar2", "User")
        )
        val nextPageUsers = listOf(
            GithubUserSummary("user3", 3L, "avatar3", "User"),
            GithubUserSummary("user4", 4L, "avatar4", "User")
        )
        whenever(repository.getUsers(null, 50)).thenReturn(initialUsers)
        whenever(repository.getUsers(2L, 50)).thenReturn(nextPageUsers)

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is UserListState.Success)
        val allUsers = (state as UserListState.Success).users
        assertEquals(4, allUsers.size)
        assertEquals("user1", allUsers[0].login)
        assertEquals("user4", allUsers[3].login)
        verify(repository).getUsers(2L, 50)
    }

    @Test
    fun `loadMoreUsers should set isLoadingMore to true while loading`() = runTest {
        // Given
        val initialUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        val nextPageUsers = listOf(GithubUserSummary("user2", 2L, "avatar2", "User"))
        whenever(repository.getUsers(null, 50)).thenReturn(initialUsers)
        whenever(repository.getUsers(1L, 50)).thenReturn(nextPageUsers)

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.loadMoreUsers()

        // Then - isLoadingMore should be true before completion
        val loadingState = viewModel.state.value
        assertTrue(loadingState is UserListState.Success)
        assertTrue((loadingState as UserListState.Success).isLoadingMore)

        advanceUntilIdle()

        // After completion, isLoadingMore should be false
        val finalState = viewModel.state.value
        assertTrue(finalState is UserListState.Success)
        assertFalse((finalState as UserListState.Success).isLoadingMore)
    }

    @Test
    fun `loadMoreUsers should do nothing when state is Loading`() = runTest {
        // Given
        val initialUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        whenever(repository.getUsers(null, 50)).thenReturn(initialUsers)

        viewModel = MainViewModel(repository)
        // Don't advance - state is still Loading

        // When
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // Then - should only call getUsers once (initial load)
        verify(repository).getUsers(null, 50)
        verify(repository, never()).getUsers(1L, 50)
    }

    @Test
    fun `loadMoreUsers should do nothing when no more pages available`() = runTest {
        // Given
        val initialUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        whenever(repository.getUsers(null, 50)).thenReturn(initialUsers)
        whenever(repository.getUsers(1L, 50)).thenReturn(emptyList())

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // First loadMore returns empty - no more pages
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // When - try to load more again
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // Then - should only call getUsers(1L) once since hasMorePages is now false
        verify(repository).getUsers(null, 50)
        verify(repository).getUsers(1L, 50)
    }

    @Test
    fun `loadMoreUsers should preserve existing data on error`() = runTest {
        // Given
        val initialUsers = listOf(
            GithubUserSummary("user1", 1L, "avatar1", "User"),
            GithubUserSummary("user2", 2L, "avatar2", "User")
        )
        whenever(repository.getUsers(null, 50)).thenReturn(initialUsers)
        whenever(repository.getUsers(2L, 50)).thenThrow(RuntimeException("Network error"))

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // Then - should still have original users, not Error state
        val state = viewModel.state.value
        assertTrue(state is UserListState.Success)
        assertEquals(initialUsers, (state as UserListState.Success).users)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun `loadMoreUsers should do nothing when list is empty`() = runTest {
        // Given
        whenever(repository.getUsers(null, 50)).thenReturn(emptyList())

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // Then - should not attempt to load more (no lastUserId available)
        verify(repository).getUsers(null, 50)
        verify(repository, never()).getUsers(0L, 50)
    }

    @Test
    fun `loadInitialUsers should reset pagination state`() = runTest {
        // Given
        val initialUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        val nextPageUsers = listOf(GithubUserSummary("user2", 2L, "avatar2", "User"))
        val refreshedUsers = listOf(GithubUserSummary("user3", 3L, "avatar3", "User"))

        whenever(repository.getUsers(null, 50))
            .thenReturn(initialUsers)
            .thenReturn(refreshedUsers)
        whenever(repository.getUsers(1L, 50)).thenReturn(nextPageUsers)

        viewModel = MainViewModel(repository)
        advanceUntilIdle()

        // Load more to get 2 users
        viewModel.loadMoreUsers()
        advanceUntilIdle()

        // When - refresh
        viewModel.loadInitialUsers()
        advanceUntilIdle()

        // Then - should have only refreshed users, not combined list
        val state = viewModel.state.value
        assertTrue(state is UserListState.Success)
        assertEquals(1, (state as UserListState.Success).users.size)
        assertEquals("user3", state.users[0].login)
    }
}
