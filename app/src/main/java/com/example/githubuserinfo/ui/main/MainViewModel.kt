package com.example.githubuserinfo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserinfo.data.GithubRepository
import com.example.githubuserinfo.data.model.GithubUserSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserListState {
    object Loading : UserListState()
    data class Success(val users: List<GithubUserSummary>, val isLoadingMore: Boolean = false) : UserListState()
    data class Error(val message: String?) : UserListState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private var isLoadingMore = false
    private var hasMorePages = true

    init {
        loadInitialUsers()
    }

    fun loadInitialUsers() {
        hasMorePages = true
        _state.value = UserListState.Loading

        viewModelScope.launch {
            try {
                val users = repository.getUsers()
                hasMorePages = users.isNotEmpty()
                _state.value = UserListState.Success(users)
            } catch (e: Exception) {
                _state.value = UserListState.Error(e.message)
            }
        }
    }

    fun loadMoreUsers() {
        val currentState = _state.value
        if (currentState !is UserListState.Success) return
        if (isLoadingMore || !hasMorePages) return

        val lastUserId = currentState.users.lastOrNull()?.id ?: return

        isLoadingMore = true
        _state.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            try {
                val newUsers = repository.getUsers(since = lastUserId)
                hasMorePages = newUsers.isNotEmpty()
                val allUsers = currentState.users + newUsers
                _state.value = UserListState.Success(allUsers, isLoadingMore = false)
            } catch (_: Exception) {
                _state.value = currentState.copy(isLoadingMore = false)
            } finally {
                isLoadingMore = false
            }
        }
    }
}

