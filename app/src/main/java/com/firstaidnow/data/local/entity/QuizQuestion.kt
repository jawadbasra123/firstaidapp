package com.firstaidnow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,   // "A", "B", "C", or "D"
    val explanation: String
)
