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
    data class Success(val users: List<GithubUserSummary>) : UserListState()
    data class Error(val message: String?) : UserListState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state.asStateFlow()

    init {
        loadInitialUsers()
    }

    fun loadInitialUsers() {
        _state.value = UserListState.Loading

        // use a coroutine to get the users
        viewModelScope.launch {
            try {
                val users = repository.getUsers()
                _state.value = UserListState.Success(users)
            } catch (e: Exception) {
                _state.value = UserListState.Error(e.message)
            }
        }
    }
}

