package com.varma.hemanshu.firetask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_layout.view.*

class FireTaskAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<FireTask> = ArrayList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FireTaskViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FireTaskViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(fireTaskList: List<FireTask>) {
        items = fireTaskList
    }

    class FireTaskViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        val userMessage = itemView.item_message
        val userName = itemView.item_user_name

        fun bind(fireTask: FireTask) {
            userMessage.text = fireTask.message
            userName.text = fireTask.username
        }
    }

}