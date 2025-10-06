package com.example.sumativa1

import android.app.Application
import org.osmdroid.config.Configuration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().userAgentValue = "Sumativa1/1.0"
    }
}