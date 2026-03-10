package com.firstaidnow.repository

import androidx.lifecycle.LiveData
import com.firstaidnow.data.local.dao.FirstAidDao
import com.firstaidnow.data.local.entity.FirstAidTopic
import com.firstaidnow.data.local.entity.QuizQuestion

class FirstAidRepository(private val dao: FirstAidDao) {

    fun getAllTopics(): LiveData<List<FirstAidTopic>> = dao.getAllTopics()

    fun getTopicById(id: Int): LiveData<FirstAidTopic?> = dao.getTopicById(id)

    fun getTopicsByCategory(category: String): LiveData<List<FirstAidTopic>> =
        dao.getTopicsByCategory(category)

    fun getCategories(): LiveData<List<String>> = dao.getCategories()

    fun searchTopics(query: String): LiveData<List<FirstAidTopic>> = dao.searchTopics(query)

    suspend fun getRandomQuizQuestions(count: Int): List<QuizQuestion> =
        dao.getRandomQuizQuestions(count)
}
