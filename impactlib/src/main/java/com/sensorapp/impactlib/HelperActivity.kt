package com.sensorapp.impactlib

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

open class HelperActivity: AppCompatActivity() {

    companion object {
        private const val TAG = "PermissionActivity"
        const val REQUEST_RECORD_AUDIO = 100
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.SettingsFragment) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("unused")
    protected fun isMicrophonePermissionGranted(): Boolean {
        val requiredPermission = Manifest.permission.RECORD_AUDIO
        return checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_GRANTED
    }

    @Suppress("unused")
    @RequiresApi(Build.VERSION_CODES.M)
    protected fun requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            onMicrophonePermissionGranted()
        } else {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
            requestPermissions(permissions, REQUEST_RECORD_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Audio permission granted :)")
                onMicrophonePermissionGranted()
            } else {
                Log.e(TAG, "Audio permission not granted :(")
                onMicrophonePermissionDenied()
            }
        }
    }

    open fun onMicrophonePermissionGranted() {
    }

    open fun onMicrophonePermissionDenied() {
    }

    @Suppress("unused")
    fun launchActivityOnButtonClick(activity: Activity, buttonId: Int, activityClass: Class<out Activity>) {
        activity.findViewById<Button>(buttonId).setOnClickListener {
            val intent = Intent(activity, activityClass)
            activity.startActivity(intent)
        }
    }
}