package com.firstaidnow.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstaidnow.databinding.ItemMessageUserBinding
import com.firstaidnow.databinding.ItemMessageBotBinding

class ChatAdapter : ListAdapter<ChatViewModel.ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 0
        private const val VIEW_TYPE_BOT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            UserViewHolder(
                ItemMessageUserBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            BotViewHolder(
                ItemMessageBotBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserViewHolder -> holder.binding.tvMessage.text = message.text
            is BotViewHolder -> holder.binding.tvMessage.text = message.text
        }
    }

    inner class UserViewHolder(val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class BotViewHolder(val binding: ItemMessageBotBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<ChatViewModel.ChatMessage>() {
        override fun areItemsTheSame(a: ChatViewModel.ChatMessage, b: ChatViewModel.ChatMessage) =
            a.timestamp == b.timestamp

        override fun areContentsTheSame(a: ChatViewModel.ChatMessage, b: ChatViewModel.ChatMessage) =
            a == b
    }
}
