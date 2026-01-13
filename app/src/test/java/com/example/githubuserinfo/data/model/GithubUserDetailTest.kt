package com.example.githubuserinfo.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GithubUserDetailTest {

    @Test
    fun `GithubUserDetail should be created with all fields`() {
        // Given
        val userDetail = GithubUserDetail(
            login = "octocat",
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

        // Then
        assertEquals("octocat", userDetail.login)
        assertEquals(1L, userDetail.id)
        assertEquals("The Octocat", userDetail.name)
        assertEquals("GitHub's mascot", userDetail.bio)
        assertEquals("https://github.com/images/error/octocat_happy.gif", userDetail.avatarUrl)
        assertEquals("https://github.com/octocat", userDetail.htmlUrl)
        assertEquals(100, userDetail.followers)
        assertEquals(50, userDetail.following)
        assertEquals(10, userDetail.publicRepos)
        assertEquals("2024-01-01T00:00:00Z", userDetail.updatedAt)
    }

    @Test
    fun `GithubUserDetail should be creatable with all optional fields null`() {
        // Given
        val userDetail = GithubUserDetail(
            login = "octocat",
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

        // Then
        assertEquals("octocat", userDetail.login)
        assertEquals(1L, userDetail.id)
        assertEquals(null, userDetail.name)
        assertEquals(null, userDetail.bio)
        assertEquals(null, userDetail.avatarUrl)
        assertEquals(null, userDetail.htmlUrl)
        assertEquals(0, userDetail.followers)
        assertEquals(0, userDetail.following)
        assertEquals(0, userDetail.publicRepos)
        assertEquals(null, userDetail.updatedAt)
    }

    @Test
    fun `hasName should return true when name is not null or blank`() {
        // Given
        val userWithName = GithubUserDetail(
            login = "octocat",
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

        // Then
        assertTrue(userWithName.name != null && userWithName.name.isNotBlank())
    }

    @Test
    fun `hasName should return false when name is null`() {
        // Given
        val userWithoutName = GithubUserDetail(
            login = "octocat",
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

        // Then
        assertTrue(userWithoutName.name == null)
    }

    @Test
    fun `GithubUserDetail instances with same fields should be equal`() {
        // Given
        val user1 = createMockUserDetail()
        val user2 = createMockUserDetail()

        // Then
        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `GithubUserDetail instances with different fields should not be equal`() {
        // Given
        val user1 = createMockUserDetail()
        val user2 = user1.copy(name = "Different Name")

        // Then
        assertFalse(user1 == user2)
    }

    @Test
    fun `GithubUserDetail copy should maintain original immutability`() {
        // Given
        val original = createMockUserDetail()

        // When
        val copied = original.copy(followers = 999)

        // Then
        assertEquals(100, original.followers)
        assertEquals(999, copied.followers)
        assertEquals(original.login, copied.login)
    }

    private fun createMockUserDetail(): GithubUserDetail {
        return GithubUserDetail(
            login = "octocat",
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
    }
}