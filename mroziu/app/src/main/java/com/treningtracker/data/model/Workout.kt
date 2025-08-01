package com.treningtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "workouts")
@Parcelize
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val date: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val notes: String = ""
) : Parcelable