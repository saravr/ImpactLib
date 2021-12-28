package com.sensorapp.impactlib

import android.content.Context
import android.media.AudioRecord
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.os.HandlerCompat
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class ImpactAudioClassifier(private val context: Context, private val notifier: ImpactAudioNotifier) {

    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var classificationInterval = 500L // how often should classification run in milli-secs
    private var handler: Handler // background thread handler to run classification

    init {
        // Create a handler to run classification in a background thread
        val handlerThread = HandlerThread("backgroundThread")
        handlerThread.start()
        handler = HandlerCompat.createAsync(handlerThread.looper)
    }

    @Suppress("unused")
    fun isAudioClassifierRunning() = (audioClassifier != null)

    @Suppress("unused")
    fun startAudioClassifier() {
        // If the audio classifier is initialized and running, do nothing.
        if (audioClassifier != null) return

        // Initialize the audio classifier
        val classifier = AudioClassifier.createFromFile(context, MODEL_FILE)
        val audioTensor = classifier.createInputTensorAudio()

        // Initialize the audio recorder
        val record = classifier.createAudioRecord()
        record.startRecording()

        // Define the classification runnable
        val run = object : Runnable {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun run() {
                //val startTime = System.currentTimeMillis()

                // Load the latest audio sample
                audioTensor.load(record)
                val output = classifier.classify(audioTensor)

                // Filter out results above a certain threshold, and sort them descendingly
                val filteredModelOutput = output[0].categories.filter {
                    it.score > MINIMUM_DISPLAY_THRESHOLD
                }.sortedBy {
                    -it.score
                }

                //val finishTime = System.currentTimeMillis()
                //Log.d(TAG, "Latency = ${finishTime - startTime}ms")
                Log.d(TAG, "Filtered o/p: $filteredModelOutput")

                val audioTypes = filteredModelOutput.joinToString {
                    it.label
                }
                notifier.reportAudioEvent(audioTypes)

                // Rerun the classification after a certain interval
                handler.postDelayed(this, classificationInterval)
            }
        }

        // Start the classification process
        handler.post(run)

        // Save the instances we just created for use later
        audioClassifier = classifier
        audioRecord = record
    }

    @Suppress("unused")
    fun stopAudioClassifier() {
        handler.removeCallbacksAndMessages(null)
        audioRecord?.stop()
        audioRecord = null
        audioClassifier = null
    }

    companion object {
        @Suppress("unused")
        const val REQUEST_RECORD_AUDIO = 1337
        private const val TAG = "ImpactAudioClassifier"
        private const val MODEL_FILE = "yamnet.tflite"
        private const val MINIMUM_DISPLAY_THRESHOLD: Float = 0.3f
    }
}

interface ImpactAudioNotifier {
    fun reportAudioEvent(audioTypes: String)
}