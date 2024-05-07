package com.typ.handtalk

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class HandTalkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}