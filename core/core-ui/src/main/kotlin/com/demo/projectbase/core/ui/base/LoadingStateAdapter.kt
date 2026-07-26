package com.demo.projectbase.core.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.projectbase.core.ui.databinding.ItemLoadingBinding

class LoadingStateAdapter : LoadStateAdapter<LoadingStateAdapter.LoadingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): LoadingViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LoadingViewHolder,
        loadState: LoadState,
    ) {
        holder.bind(loadState)
    }

    class LoadingViewHolder(private val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.root.isVisible = loadState is LoadState.Loading
        }
    }
}
