package com.sandymist.impactlib

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import android.util.Log
import kotlin.math.abs

class ImpactMotionClassifier(context: Context, private val notifier: ImpactMotionNotifier): SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerSensor: Sensor? = null
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var deltaX = 0f
    private var deltaY = 0f
    private var deltaZ = 0f
    private var vibrateThreshold = 0f

    companion object {
        private const val TAG = "ImpactMotionClassifier"
    }
    init {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            vibrateThreshold = 0.00f//     it.maximumRange / 2
        }
    }

    fun startMotionClassifier() {
        accelerometerSensor.also {
            sensorManager.registerListener(this, it, 30000000)//SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopMotionClassifier() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        //val values = event.values?.joinToString(",")
        //Log.d(TAG, "+++++ SENSOR CHANGED: $values")

        // get the change of the x,y,z values of the accelerometer
        deltaX = abs(lastX - event.values[0])
        deltaY = abs(lastY - event.values[1])
        deltaZ = abs(lastZ - event.values[2])

        val threshold = 0.005
        // if the change is below 2, it is just plain noise
        if (deltaX < threshold) deltaX = 0f
        if (deltaY < threshold) deltaY = 0f
        if (deltaZ < threshold) deltaZ = 0f

        // set the last know values of x,y,z
        lastX = event.values[0]
        lastY = event.values[1]
        lastZ = event.values[2]
        reportMovement()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "+++++ ON ACC CHANGED")
    }

    private fun reportMovement() {
        //Log.d(TAG, "++++ EVENT, $deltaX, $deltaY, $deltaZ, $vibrateThreshold")
        if (deltaX > vibrateThreshold || deltaY > vibrateThreshold || deltaZ > vibrateThreshold) {
            val onUIThread = Looper.myLooper() == Looper.getMainLooper()
            Log.d(TAG, "++++ REPORT MOVEMENT - onUIThread $onUIThread")
            notifier.reportMotionEvent("$deltaX / $deltaY / $deltaZ")
        }
    }
}

interface ImpactMotionNotifier {
    fun reportMotionEvent(motionEvent: String)
}