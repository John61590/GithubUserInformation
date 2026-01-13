package com.example.githubuserinfo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserinfo.R
import com.example.githubuserinfo.data.model.GithubUserSummary
import com.example.githubuserinfo.databinding.ActivityMainBinding
import com.example.githubuserinfo.ui.detail.UserDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val adapter = UserListAdapter { user -> openUserDetail(user) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fix the last item in recycler view being covered by the 3-button navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.usersRecyclerView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupSwipeRefresh()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = adapter
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.swipeRefreshLayout.isRefreshing = false

                when (state) {
                    is UserListState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyTextView.visibility = View.GONE
                        binding.usersRecyclerView.visibility = View.GONE
                    }

                    is UserListState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.usersRecyclerView.visibility = View.VISIBLE
                        adapter.submitList(state.users)
                        if (state.users.isEmpty()) {
                            binding.emptyTextView.text = getString(R.string.empty_state)
                            binding.emptyTextView.visibility = View.VISIBLE
                            binding.usersRecyclerView.visibility = View.GONE
                        } else {
                            binding.emptyTextView.visibility = View.GONE
                            binding.usersRecyclerView.visibility = View.VISIBLE
                        }
                    }

                    is UserListState.Error -> {
                        binding.usersRecyclerView.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        binding.emptyTextView.text = getString(R.string.error_message)
                        binding.emptyTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadInitialUsers()
        }
    }

    private fun openUserDetail(user: GithubUserSummary) {
        val intent = Intent(this, UserDetailActivity::class.java).apply {
            putExtra(UserDetailActivity.EXTRA_LOGIN, user.login)
            putExtra(UserDetailActivity.EXTRA_AVATAR_URL, user.avatarUrl)
        }
        startActivity(intent)
    }
}

