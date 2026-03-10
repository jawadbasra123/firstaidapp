package com.firstaidnow.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstaidnow.R
import com.firstaidnow.data.local.entity.FirstAidTopic
import com.firstaidnow.databinding.ItemTopicBinding

class TopicAdapter(
    private val onClick: (FirstAidTopic) -> Unit
) : ListAdapter<FirstAidTopic, TopicAdapter.TopicViewHolder>(TopicDiffCallback()) {

    inner class TopicViewHolder(val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = getItem(position)
        holder.binding.apply {
            tvTopicTitle.text = topic.title
            tvTopicSummary.text = topic.summary
            tvTopicCategory.text = topic.category

            val severityColor = when (topic.severity) {
                "critical" -> R.color.severity_critical
                "high" -> R.color.severity_high
                "medium" -> R.color.severity_medium
                else -> R.color.severity_low
            }
            tvSeverity.text = topic.severity.uppercase()
            tvSeverity.setTextColor(holder.itemView.context.getColor(severityColor))

            root.setOnClickListener { onClick(topic) }
        }
    }

    class TopicDiffCallback : DiffUtil.ItemCallback<FirstAidTopic>() {
        override fun areItemsTheSame(oldItem: FirstAidTopic, newItem: FirstAidTopic) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FirstAidTopic, newItem: FirstAidTopic) =
            oldItem == newItem
    }
}
