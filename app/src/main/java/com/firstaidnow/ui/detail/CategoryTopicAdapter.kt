package com.firstaidnow.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstaidnow.data.local.entity.FirstAidTopic
import com.firstaidnow.databinding.ItemTopicBinding

class CategoryTopicAdapter(
    private val onClick: (FirstAidTopic) -> Unit
) : ListAdapter<FirstAidTopic, CategoryTopicAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = getItem(position)
        holder.binding.apply {
            tvTopicTitle.text = topic.title
            tvTopicSummary.text = topic.summary
            tvTopicCategory.text = topic.category
            tvSeverity.text = topic.severity.uppercase()
            root.setOnClickListener { onClick(topic) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FirstAidTopic>() {
        override fun areItemsTheSame(a: FirstAidTopic, b: FirstAidTopic) = a.id == b.id
        override fun areContentsTheSame(a: FirstAidTopic, b: FirstAidTopic) = a == b
    }
}
