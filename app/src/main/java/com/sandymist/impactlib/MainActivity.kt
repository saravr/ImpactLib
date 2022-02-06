package com.sandymist.impactlib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sensorapp.impactlib.HelperActivity

class MainActivity : HelperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}