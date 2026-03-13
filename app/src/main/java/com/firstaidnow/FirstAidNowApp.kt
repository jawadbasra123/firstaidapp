package com.firstaidnow

import android.app.Application
import com.firstaidnow.data.remote.RetrofitClient
import com.firstaidnow.repository.FirstAidRepository

class FirstAidNowApp : Application() {

    // Database instance
    val database by lazy { com.firstaidnow.data.local.database.FirstAidDatabase.getInstance(this) }
    
    // Repository with injected Gemini API
    val repository by lazy { 
        FirstAidRepository(
            database.firstAidDao(),
            RetrofitClient.geminiApi
        ) 
    }

    override fun onCreate() {
        super.onCreate()
    }
}
