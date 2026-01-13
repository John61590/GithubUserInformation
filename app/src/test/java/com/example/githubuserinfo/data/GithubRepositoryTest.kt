package com.example.githubuserinfo.data

import com.example.githubuserinfo.data.model.GithubUserDetail
import com.example.githubuserinfo.data.model.GithubUserSummary
import com.example.githubuserinfo.data.remote.GithubApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GithubRepositoryTest {

    private lateinit var apiService: GithubApiService
    private lateinit var repository: GithubRepository

    @Before
    fun setup() {
        apiService = mock()
        repository = GithubRepository(apiService)
    }

    @Test
    fun `getUsers should call API service with default parameters`() = runTest {
        // Given
        val mockUsers = listOf(
            GithubUserSummary("user1", 1L, "avatar1", "User"),
            GithubUserSummary("user2", 2L, "avatar2", "User")
        )
        whenever(apiService.getUsers(null, 50)).thenReturn(mockUsers)

        // When
        val result = repository.getUsers()

        // Then
        assertEquals(mockUsers, result)
        verify(apiService).getUsers(null, 50)
    }

    @Test
    fun `getUsers should call API service with custom parameters`() = runTest {
        // Given
        val since = 100L
        val perPage = 30
        val mockUsers = listOf(GithubUserSummary("user1", 1L, "avatar1", "User"))
        whenever(apiService.getUsers(since, perPage)).thenReturn(mockUsers)

        // When
        val result = repository.getUsers(since, perPage)

        // Then
        assertEquals(mockUsers, result)
        verify(apiService).getUsers(since, perPage)
    }

    @Test
    fun `getUsers should propagate exception from API service`() = runTest {
        // Given
        val error = RuntimeException("API error")
        whenever(apiService.getUsers(null, 50)).thenThrow(error)

        // When & Then
        try {
            repository.getUsers()
            assert(false) { "Should have thrown exception" }
        } catch (e: RuntimeException) {
            assertEquals("API error", e.message)
        }
    }

    @Test
    fun `getUserDetail should call API service with login`() = runTest {
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
        whenever(apiService.getUserDetail(login)).thenReturn(mockUserDetail)

        // When
        val result = repository.getUserDetail(login)

        // Then
        assertEquals(mockUserDetail, result)
        verify(apiService).getUserDetail(login)
    }

    @Test
    fun `getUserDetail should propagate exception from API service`() = runTest {
        // Given
        val login = "nonexistent"
        val error = RuntimeException("User not found")
        whenever(apiService.getUserDetail(login)).thenThrow(error)

        // When & Then
        try {
            repository.getUserDetail(login)
            assert(false) { "Should have thrown exception" }
        } catch (e: RuntimeException) {
            assertEquals("User not found", e.message)
        }
    }

    @Test
    fun `getUserDetail should handle empty login`() = runTest {
        // Given
        val login = ""
        val error = IllegalArgumentException("Login cannot be empty")
        whenever(apiService.getUserDetail(login)).thenThrow(error)

        // When & Then
        try {
            repository.getUserDetail(login)
            assert(false) { "Should have thrown exception" }
        } catch (e: IllegalArgumentException) {
            assertEquals("Login cannot be empty", e.message)
        }
    }
}
