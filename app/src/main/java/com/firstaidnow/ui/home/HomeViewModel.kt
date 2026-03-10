package com.firstaidnow.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.firstaidnow.FirstAidNowApp
import com.firstaidnow.data.local.entity.FirstAidTopic

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FirstAidNowApp).repository

    private val _searchQuery = MutableLiveData("")

    val topics: LiveData<List<FirstAidTopic>> = _searchQuery.switchMap { query ->
        if (query.isNullOrBlank()) {
            repository.getAllTopics()
        } else {
            repository.searchTopics(query)
        }
    }

    val categories: LiveData<List<String>> = repository.getCategories()

    fun search(query: String) {
        _searchQuery.value = query
    }

    data class CategoryItem(
        val name: String,
        val iconRes: Int,
        val colorRes: Int
    )
}
