package com.example.githubuserinfo.data.model

import com.squareup.moshi.Json

data class GithubUserSummary(
    val login: String,
    val id: Long,
    @Json(name = "avatar_url") val avatarUrl: String?,
    val type: String?
)

