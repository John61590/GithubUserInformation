package com.example.githubuserinfo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.layoutManager = layoutManager
        binding.usersRecyclerView.adapter = adapter

        binding.usersRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return // Only trigger on scroll down

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Load more when user is 5 items away from the end
                if ((visibleItemCount + firstVisibleItemPosition + 5) >= totalItemCount) {
                    viewModel.loadMoreUsers()
                }
            }
        })
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

