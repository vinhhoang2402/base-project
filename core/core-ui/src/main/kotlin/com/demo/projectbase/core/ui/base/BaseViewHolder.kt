package com.demo.projectbase.core.ui.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<Item>(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(
        item: Item,
        position: Int,
    )

    open fun bind(
        item: Item,
        payloads: MutableList<Any>,
        position: Int,
    ) = bind(item, position)
}
