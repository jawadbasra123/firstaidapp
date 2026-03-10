package com.firstaidnow.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.firstaidnow.data.local.entity.FirstAidTopic
import com.firstaidnow.data.local.entity.QuizQuestion

@Dao
interface FirstAidDao {

    @Query("SELECT * FROM first_aid_topics ORDER BY category, title")
    fun getAllTopics(): LiveData<List<FirstAidTopic>>

    @Query("SELECT * FROM first_aid_topics WHERE id = :id")
    fun getTopicById(id: Int): LiveData<FirstAidTopic?>

    @Query("SELECT * FROM first_aid_topics WHERE category = :category ORDER BY title")
    fun getTopicsByCategory(category: String): LiveData<List<FirstAidTopic>>

    @Query("SELECT DISTINCT category FROM first_aid_topics ORDER BY category")
    fun getCategories(): LiveData<List<String>>

    @Query("SELECT * FROM first_aid_topics WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%'")
    fun searchTopics(query: String): LiveData<List<FirstAidTopic>>

    @Query("SELECT * FROM quiz_questions ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuizQuestions(count: Int): List<QuizQuestion>
}
