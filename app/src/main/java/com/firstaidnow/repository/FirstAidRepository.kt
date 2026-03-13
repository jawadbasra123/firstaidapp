package com.firstaidnow.repository

import androidx.lifecycle.LiveData
import com.firstaidnow.data.local.dao.FirstAidDao
import com.firstaidnow.data.local.entity.FirstAidTopic
import com.firstaidnow.data.local.entity.QuizQuestion
import com.firstaidnow.data.remote.Content
import com.firstaidnow.data.remote.GeminiApiService
import com.firstaidnow.data.remote.GeminiRequest
import com.firstaidnow.data.remote.Part

class FirstAidRepository(
    private val dao: FirstAidDao,
    private val api: GeminiApiService
) {

    fun getAllTopics(): LiveData<List<FirstAidTopic>> = dao.getAllTopics()

    fun getTopicById(id: Int): LiveData<FirstAidTopic?> = dao.getTopicById(id)

    fun getTopicsByCategory(category: String): LiveData<List<FirstAidTopic>> =
        dao.getTopicsByCategory(category)

    fun getCategories(): LiveData<List<String>> = dao.getCategories()

    fun searchTopics(query: String): LiveData<List<FirstAidTopic>> = dao.searchTopics(query)

    suspend fun getRandomQuizQuestions(count: Int): List<QuizQuestion> =
        dao.getRandomQuizQuestions(count)

    suspend fun getGeminiResponse(apiKey: String, userPrompt: String): String? {
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are a professional first aid assistant. Provide concise, accurate, and actionable medical advice for common emergencies. If a situation is life-threatening, advise the user to call 911 immediately.")))
        )
        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
