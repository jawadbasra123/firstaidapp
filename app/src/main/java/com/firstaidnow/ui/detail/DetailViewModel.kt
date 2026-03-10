package com.firstaidnow.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.firstaidnow.FirstAidNowApp
import com.firstaidnow.data.local.entity.FirstAidTopic

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FirstAidNowApp).repository

    fun getTopicById(id: Int): LiveData<FirstAidTopic?> = repository.getTopicById(id)

    fun getTopicsByCategory(category: String): LiveData<List<FirstAidTopic>> =
        repository.getTopicsByCategory(category)
}
