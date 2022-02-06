package com.sensorapp.impactlib

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrefItem(
    val key: String,
    val title: String,
    val default: Float
): Parcelable