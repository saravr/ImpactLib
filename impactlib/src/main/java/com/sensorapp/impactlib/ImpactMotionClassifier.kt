package com.sensorapp.impactlib

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import android.util.Log
import kotlin.math.abs

data class MotionData(
    val deltaX: Float,
    val deltaY: Float,
    val deltaZ: Float
)

class ImpactMotionClassifier(context: Context, private val notifier: ImpactMotionNotifier): SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerSensor: Sensor? = null
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var deltaX = 0f
    private var deltaY = 0f
    private var deltaZ = 0f

    var reportingThreshold = 0f

    var noiseThreshold = DEFAULT_NOISE_THRESHOLD

    companion object {
        private const val TAG = "ImpactMotionClassifier"
        private const val DEFAULT_NOISE_THRESHOLD = 0.05
    }

    init {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            reportingThreshold = 0.00f//     it.maximumRange / 2
        }
    }

    @Suppress("unused")
    fun startMotionClassifier() {
        accelerometerSensor.also {
            sensorManager.registerListener(this, it, 30000000)//SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    @Suppress("unused")
    fun stopMotionClassifier() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        //val values = event.values?.joinToString(",")
        //Log.d(TAG, "+++++ SENSOR CHANGED: $values")

        // get the change of the x,y,z values of the accelerometer
        deltaX = lastX - event.values[0]
        deltaY = lastY - event.values[1]
        deltaZ = lastZ - event.values[2]

        // if the change is below noiseThreshold, it is just plain noise
        if (abs(deltaX) < noiseThreshold) deltaX = 0f
        if (abs(deltaY) < noiseThreshold) deltaY = 0f
        if (abs(deltaZ) < noiseThreshold) deltaZ = 0f

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
        if (abs(deltaX) > reportingThreshold || abs(deltaY) > reportingThreshold || abs(deltaZ) > reportingThreshold) {
            val onUIThread = Looper.myLooper() == Looper.getMainLooper()
            Log.d(TAG, "++++ REPORT MOVEMENT: $deltaX / $deltaY / $deltaZ - [onUIThread $onUIThread]")
            notifier.reportMotionEvent(MotionData(deltaX, deltaY, deltaZ))
        }
    }
}

interface ImpactMotionNotifier {
    fun reportMotionEvent(motionData: MotionData)
}