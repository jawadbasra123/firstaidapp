package com.firstaidnow.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firstaidnow.databinding.ItemStepBinding

class StepAdapter(
    private val steps: List<String>
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    inner class StepViewHolder(val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.binding.tvStepNumber.text = "${position + 1}"
        holder.binding.tvStepText.text = steps[position]
    }

    override fun getItemCount() = steps.size
}
