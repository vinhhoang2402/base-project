package com.demo.projectbase.feature.home.presentation

import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.projectbase.core.ui.base.BaseMviFragment
import com.demo.projectbase.feature.home.R
import com.demo.projectbase.feature.home.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseMviFragment<FragmentHomeBinding, HomeContract.Intent, HomeContract.State, HomeContract.Effect>(
    FragmentHomeBinding::inflate,
) {
    override val viewModel: HomeViewModel by viewModel()

    private val movieAdapter = MovieAdapter()

    override fun setupViews() {
        binding.toolbar.inflateMenu(R.menu.menu_home)
        binding.rvMovies.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moviesPager.collectLatest { pagingData ->
                movieAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            movieAdapter.loadStateFlow.collectLatest { loadStates ->
                val refresh = loadStates.refresh
                val itemCount = movieAdapter.itemCount
                val hasError = refresh is LoadState.Error
                val isEmpty = refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        itemCount == 0
                val showFullScreenLoading = itemCount == 0 && !hasError && !isEmpty

                binding.progressBar.isVisible = showFullScreenLoading
                binding.rvMovies.isVisible = itemCount > 0
                binding.layoutError.isVisible = hasError && itemCount == 0
                if (hasError) {
                    binding.tvError.text = (refresh as? LoadState.Error)?.error?.message ?: "Failed to load movies"
                }
            }
        }
    }

    override fun setupListeners() {
        binding.btnRetry.setOnClickListener { movieAdapter.retry() }
        binding.toolbar.setOnMenuItemClickListener { item -> onMenuItemClick(item) }
    }

    override fun renderState(state: HomeContract.State) {
        binding.toolbar.menu.findItem(R.id.action_login)?.isVisible = !state.isLoggedIn
        binding.toolbar.menu.findItem(R.id.action_logout)?.isVisible = state.isLoggedIn
    }

    override fun handleEffect(effect: HomeContract.Effect) {
        when (effect) {
            HomeContract.Effect.NavigateToLogin -> {
                // findNavController().navigate(R.id.action_home_to_login)
            }
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> { viewModel.handleIntent(HomeContract.Intent.Logout); true }
            R.id.action_login -> { viewModel.handleIntent(HomeContract.Intent.Login); true }
            else -> false
        }
    }
}
