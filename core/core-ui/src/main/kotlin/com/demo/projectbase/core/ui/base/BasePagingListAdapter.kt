package com.demo.projectbase.core.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingListAdapter<Item : Any>(
    diffCallback: DiffUtil.ItemCallback<Item>,
) : PagingDataAdapter<Item, RecyclerView.ViewHolder>(diffCallback) {

    fun withLoadingFooter(): ConcatAdapter = withLoadStateFooter(LoadingStateAdapter())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateViewHolder(viewType, LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as? BaseViewHolder<Item>)?.bind(it, position) }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        getItem(position)?.let { (holder as? BaseViewHolder<Item>)?.bind(it, payloads, position) }
    }

    abstract fun onCreateViewHolder(viewType: Int, layoutInflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder
}
