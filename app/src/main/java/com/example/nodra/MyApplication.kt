package com.example.nodra

import android.app.Application
import com.example.nodrah_project.AccessibilitySettings
import com.example.nodrah_project.AccessibilitySettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyApplication : Application() {

     val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var accessibilitySettingsManager: AccessibilitySettingsManager
        private set

    lateinit var accessibilitySettings: AccessibilitySettings
        private set

    override fun onCreate() {
        super.onCreate()
        accessibilitySettingsManager = AccessibilitySettingsManager(this)

        //loads the initial settings when the app starts
        applicationScope.launch {
            accessibilitySettingsManager
                .accessibilitySettingsFlow
                .collect { settings ->
                    accessibilitySettings = settings

                }
        }
    }
}