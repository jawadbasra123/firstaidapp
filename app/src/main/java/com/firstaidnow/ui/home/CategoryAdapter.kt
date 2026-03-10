package com.firstaidnow.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firstaidnow.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<HomeViewModel.CategoryItem>,
    private val onClick: (HomeViewModel.CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        holder.binding.apply {
            tvCategoryName.text = item.name
            ivCategoryIcon.setImageDrawable(
                ContextCompat.getDrawable(root.context, item.iconRes)
            )
            cardCategory.setCardBackgroundColor(
                ContextCompat.getColor(root.context, item.colorRes)
            )
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount() = categories.size
}
