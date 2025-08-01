package com.treningtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "body_measurements")
@Parcelize
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val weight: Float? = null, // kg
    val waist: Float? = null, // cm
    val chest: Float? = null, // cm
    val biceps: Float? = null, // cm
    val forearm: Float? = null, // cm
    val thigh: Float? = null, // cm
    val calf: Float? = null, // cm
    val notes: String = ""
) : Parcelable