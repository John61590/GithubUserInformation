package com.example.githubuserinfo.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GithubUserSummaryTest {

    @Test
    fun `GithubUserSummary should be created with all fields`() {
        // Given
        val login = "octocat"
        val id = 1L
        val avatarUrl = "https://github.com/images/error/octocat_happy.gif"
        val type = "User"

        // When
        val user = GithubUserSummary(login, id, avatarUrl, type)

        // Then
        assertEquals(login, user.login)
        assertEquals(id, user.id)
        assertEquals(avatarUrl, user.avatarUrl)
        assertEquals(type, user.type)
    }

    @Test
    fun `GithubUserSummary should be creatable with null avatarUrl`() {
        // Given
        val login = "octocat"
        val id = 1L
        val avatarUrl = null
        val type = "User"

        // When
        val user = GithubUserSummary(login, id, avatarUrl, type)

        // Then
        assertEquals(login, user.login)
        assertEquals(id, user.id)
        assertEquals(null, user.avatarUrl)
        assertEquals(type, user.type)
    }

    @Test
    fun `GithubUserSummary copy should create a new instance with modified field`() {
        // Given
        val original = GithubUserSummary("user1", 1L, "avatar1", "User")

        // When
        val copied = original.copy(login = "user2")

        // Then
        assertNotEquals(original.login, copied.login)
        assertEquals("user1", original.login)
        assertEquals("user2", copied.login)
        assertEquals(original.id, copied.id)
        assertEquals(original.avatarUrl, copied.avatarUrl)
        assertEquals(original.type, copied.type)
    }

    @Test
    fun `GithubUserSummary instances with same fields should be equal`() {
        // Given
        val user1 = GithubUserSummary("octocat", 1L, "avatar1", "User")
        val user2 = GithubUserSummary("octocat", 1L, "avatar1", "User")

        // Then
        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `GithubUserSummary instances with different fields should not be equal`() {
        // Given
        val user1 = GithubUserSummary("octocat", 1L, "avatar1", "User")
        val user2 = GithubUserSummary("octocat", 1L, "avatar2", "User")

        // Then
        assertNotEquals(user1, user2)
        assertNotEquals(user1.hashCode(), user2.hashCode())
    }
}