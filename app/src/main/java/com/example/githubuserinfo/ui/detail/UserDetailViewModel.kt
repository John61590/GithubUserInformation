package com.example.githubuserinfo.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserinfo.data.GithubRepository
import com.example.githubuserinfo.data.model.GithubUserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserDetailState {
    object Loading : UserDetailState()
    data class Success(val detail: GithubUserDetail) : UserDetailState()
    data class Error(val message: String?) : UserDetailState()
}

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val state: StateFlow<UserDetailState> = _state.asStateFlow()

    fun loadUser(login: String) {
        _state.value = UserDetailState.Loading
        viewModelScope.launch {
            try {
                val detail = repository.getUserDetail(login)
                _state.value = UserDetailState.Success(detail)
            } catch (e: Exception) {
                _state.value = UserDetailState.Error(e.message)
            }
        }
    }
}

