package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_sessions")
data class RunSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalDistanceMeters: Double,
    val durationSeconds: Long,
    val avgSpeedKmh: Double,
    val avgPaceSecondsPerKm: Long,
    val caloriesBurned: Int,
    val raceTargetMeters: Double,
    val trackSizeMeters: Double,
    val targetLapSeconds: Double,
    val lapsCompleted: Int,
    val totalLapsTarget: Int,
    val splitsJson: String, // Serialized list of LapSplit
    val isCompletedTarget: Boolean = false
)

data class LapSplit(
    val lapNumber: Int,
    val lapDistanceMeters: Double,
    val lapDurationSeconds: Long,
    val targetLapSeconds: Double,
    val diffSeconds: Double,
    val isFast: Boolean
)
