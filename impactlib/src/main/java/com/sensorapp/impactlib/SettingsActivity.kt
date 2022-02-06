package com.sensorapp.impactlib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsFragment = SettingsFragment.newInstance(intent.extras)
        settingsFragment.arguments = intent.extras

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fl_container, settingsFragment)
            .commit()
    }
}