package com.example.githubuserinfo.data

import com.example.githubuserinfo.data.model.GithubUserDetail
import com.example.githubuserinfo.data.model.GithubUserSummary
import com.example.githubuserinfo.data.remote.GithubApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val apiService: GithubApiService
) {

    suspend fun getUsers(since: Long? = null, perPage: Int = 50): List<GithubUserSummary> {
        return apiService.getUsers(since, perPage)
    }

    suspend fun getUserDetail(login: String): GithubUserDetail {
        return apiService.getUserDetail(login)
    }
}

