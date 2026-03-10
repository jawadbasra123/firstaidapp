package com.firstaidnow

import android.app.Application

class FirstAidNowApp : Application() {

    val database by lazy { com.firstaidnow.data.local.database.FirstAidDatabase.getInstance(this) }
    val repository by lazy { com.firstaidnow.repository.FirstAidRepository(database.firstAidDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
