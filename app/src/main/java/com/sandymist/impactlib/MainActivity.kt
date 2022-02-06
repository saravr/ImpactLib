package com.sandymist.impactlib

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.sensorapp.impactlib.HelperActivity
import com.sensorapp.impactlib.PrefItem

class MainActivity : HelperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefItems.addAll(
            listOf(
                PrefItem("amp-threshold", "Amplitude Threshold", 1000f),
                PrefItem("noise-threshold", "Noise Threshold", 0.0f),
                PrefItem("reporting-threshold", "Reporting Threshold", 0.12f)
            )
        )

        val ampTest = PreferenceManager.getDefaultSharedPreferences(this).getString("amp-threshold", "111")
        Log.e("++++", "++++ AMPTEST $ampTest")
    }
}