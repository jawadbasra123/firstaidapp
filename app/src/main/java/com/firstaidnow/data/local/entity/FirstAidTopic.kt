package com.firstaidnow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "first_aid_topics")
data class FirstAidTopic(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val title: String,
    val icon: String,
    val summary: String,
    val steps: String,       // JSON array of step strings
    val warnings: String,    // JSON array of warning strings
    val severity: String     // "low", "medium", "high", "critical"
)
