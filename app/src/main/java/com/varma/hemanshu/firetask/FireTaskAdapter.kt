package com.varma.hemanshu.firetask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.varma.hemanshu.firetask.databinding.ListItemLayoutBinding

class FireTaskAdapter : ListAdapter<FireTask, FireTaskAdapter.ViewHolder>(FireTaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(val binding: ListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FireTask) {
            binding.fireTaskData = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemLayoutBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

class FireTaskDiffCallback : DiffUtil.ItemCallback<FireTask>() {
    override fun areItemsTheSame(oldItem: FireTask, newItem: FireTask): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: FireTask, newItem: FireTask): Boolean {
        return oldItem == newItem
    }
}