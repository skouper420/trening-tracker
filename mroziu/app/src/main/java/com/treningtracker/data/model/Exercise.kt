package com.treningtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "exercises")
@Parcelize
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val usesWeight: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) : Parcelable