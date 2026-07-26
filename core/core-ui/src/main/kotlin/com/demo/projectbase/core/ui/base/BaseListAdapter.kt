package com.demo.projectbase.core.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<Item>(
    diffCallback: DiffUtil.ItemCallback<Item>,
) : ListAdapter<Item, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return onCreateViewHolder(viewType, LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        (holder as? BaseViewHolder<Item>)?.bind(getItem(position), position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        (holder as? BaseViewHolder<Item>)?.bind(getItem(position), payloads, position)
    }

    abstract fun onCreateViewHolder(
        viewType: Int,
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
    ): RecyclerView.ViewHolder
}
