package com.example.gridlayoutmanager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gridlayoutmanager.ItemsAdapter.ItemViewHolder
import com.example.gridlayoutmanager.data.DataItem
import com.example.gridlayoutmanager.databinding.ViewListItemBinding

class ItemsAdapter: ListAdapter<DataItem, ItemViewHolder>(ListItemsDiffUtilsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            itemBinding = ViewListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(
        private val itemBinding: ViewListItemBinding
    ): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: DataItem) {
            itemBinding.viewTitleText.text = item.itemValue
        }
    }
}

private class ListItemsDiffUtilsCallback: DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
        oldItem.itemValue == newItem.itemValue

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
        oldItem.itemValue == newItem.itemValue
}

