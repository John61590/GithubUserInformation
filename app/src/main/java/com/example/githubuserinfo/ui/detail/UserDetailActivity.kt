package com.example.githubuserinfo.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.githubuserinfo.R
import com.example.githubuserinfo.databinding.ActivityUserDetailBinding
import com.example.githubuserinfo.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailBinding

    private val viewModel: UserDetailViewModel by viewModels()

    private var login: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        login = intent.getStringExtra(EXTRA_LOGIN)
        val avatarUrl = intent.getStringExtra(EXTRA_AVATAR_URL)

        setupToolbar()
        setupSwipeRefresh()
        observeState()

        avatarUrl?.let {
            binding.detailAvatarImageView.load(it) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }

        if (!login.isNullOrBlank()) {
            viewModel.loadUser(login!!)
        } else {
            binding.detailErrorTextView.visibility = View.VISIBLE
        }
    }

    private fun setupSwipeRefresh() {
        binding.detailSwipeRefreshLayout.setOnRefreshListener {
            login?.let { viewModel.loadUser(it) }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.detailToolbar)
        binding.detailToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
        binding.detailToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is UserDetailState.Loading -> {
                        binding.detailErrorTextView.visibility = View.GONE
                        // don't show progress bar if doing swipe to refresh
                        if (!binding.detailSwipeRefreshLayout.isRefreshing) {
                            binding.detailContentLayout.visibility = View.GONE
                            binding.detailProgressBar.visibility = View.VISIBLE
                        }
                    }

                    is UserDetailState.Success -> {
                        binding.detailProgressBar.visibility = View.GONE
                        binding.detailContentLayout.visibility = View.VISIBLE
                        binding.detailSwipeRefreshLayout.isRefreshing = false
                        binding.detailErrorTextView.visibility = View.GONE

                        val user = state.detail
                        val displayName = user.name ?: user.login

                        binding.detailNameTextView.text = displayName
                        binding.detailToolbar.title = user.login
                        binding.detailLoginTextView.text = user.login
                        if (user.bio.isNullOrBlank()) {
                            binding.detailBioTextView.visibility = View.GONE
                        } else {
                            binding.detailBioTextView.text = user.bio
                            binding.detailBioTextView.visibility = View.VISIBLE
                        }
                        binding.detailFollowersTextView.text =
                            getString(R.string.followers_format, user.followers)
                        binding.detailFollowingTextView.text =
                            getString(R.string.following_format, user.following)
                        binding.detailReposTextView.text =
                            getString(R.string.public_repos_format, user.publicRepos)
                        user.updatedAt?.let {
                            binding.detailLastUpdatedTextView.text =
                                getString(R.string.last_updated, DateUtils.formatIsoDate(this@UserDetailActivity, it))
                        }
                    }

                    is UserDetailState.Error -> {
                        binding.detailProgressBar.visibility = View.GONE
                        binding.detailContentLayout.visibility = View.GONE
                        binding.detailSwipeRefreshLayout.isRefreshing = false
                        binding.detailErrorTextView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_LOGIN = "extra_login"
        const val EXTRA_AVATAR_URL = "extra_avatar_url"
    }
}

