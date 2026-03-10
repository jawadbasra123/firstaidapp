package com.firstaidnow.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firstaidnow.databinding.ItemWarningBinding

class WarningAdapter(
    private val warnings: List<String>
) : RecyclerView.Adapter<WarningAdapter.WarningViewHolder>() {

    inner class WarningViewHolder(val binding: ItemWarningBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarningViewHolder {
        val binding = ItemWarningBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WarningViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WarningViewHolder, position: Int) {
        holder.binding.tvWarningText.text = warnings[position]
    }

    override fun getItemCount() = warnings.size
}
