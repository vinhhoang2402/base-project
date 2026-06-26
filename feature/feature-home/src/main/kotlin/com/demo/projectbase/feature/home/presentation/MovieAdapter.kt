package com.demo.projectbase.feature.home.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.demo.projectbase.core.ui.base.BasePagingListAdapter
import com.demo.projectbase.core.ui.base.BaseViewHolder
import com.demo.projectbase.feature.home.databinding.ItemMovieBinding
import com.demo.projectbase.feature.home.domain.model.Movie

class MovieAdapter : BasePagingListAdapter<Movie>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(viewType: Int, layoutInflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder {
        return MovieViewHolder(ItemMovieBinding.inflate(layoutInflater, parent, false))
    }

    class MovieViewHolder(private val binding: ItemMovieBinding) : BaseViewHolder<Movie>(binding) {
        override fun bind(item: Movie, position: Int) {
            binding.tvTitle.text = item.title
            binding.tvRating.text = String.format("%.1f", item.voteAverage)
            binding.ivPoster.load(item.posterUrl)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(old: Movie, new: Movie) = old.id == new.id
            override fun areContentsTheSame(old: Movie, new: Movie) = old == new
        }
    }
}
