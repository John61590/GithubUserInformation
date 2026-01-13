package com.example.githubuserinfo.data.remote

import com.example.githubuserinfo.data.model.GithubUserDetail
import com.example.githubuserinfo.data.model.GithubUserSummary
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    // https://api.github.com/users
    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Long? = null,
        @Query("per_page") perPage: Int = 50
    ): List<GithubUserSummary>

    // https://api.github.com/users/{user_login}
    @GET("users/{login}")
    suspend fun getUserDetail(
        @Path("login") login: String
    ): GithubUserDetail
}

