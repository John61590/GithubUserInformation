package com.example.githubuserinfo.data.model

import com.squareup.moshi.Json

data class GithubUserDetail(
    val login: String,
    val id: Long,
    val name: String?,
    val bio: String?,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "html_url") val htmlUrl: String?,
    val followers: Int,
    val following: Int,
    @Json(name = "public_repos") val publicRepos: Int,
    @Json(name = "updated_at") val updatedAt: String?
)

