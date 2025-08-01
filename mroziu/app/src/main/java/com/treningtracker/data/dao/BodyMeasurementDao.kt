package com.treningtracker.data.dao

import androidx.room.*
import com.treningtracker.data.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    
    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>
    
    @Query("SELECT * FROM body_measurements WHERE id = :id")
    suspend fun getMeasurementById(id: Long): BodyMeasurement?
    
    @Query("SELECT * FROM body_measurements WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getMeasurementsByDateRange(startDate: Long, endDate: Long): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE weight IS NOT NULL ORDER BY date ASC")
    suspend fun getWeightMeasurements(): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE waist IS NOT NULL ORDER BY date ASC")
    suspend fun getWaistMeasurements(): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE chest IS NOT NULL ORDER BY date ASC")
    suspend fun getChestMeasurements(): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE biceps IS NOT NULL ORDER BY date ASC")
    suspend fun getBicepsMeasurements(): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE forearm IS NOT NULL ORDER BY date ASC")
    suspend fun getForearmMeasurements(): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE thigh IS NOT NULL ORDER BY date ASC")
    suspend fun getThighMeasurements(): List<BodyMeasurement>
    
    @Query("SELECT * FROM body_measurements WHERE calf IS NOT NULL ORDER BY date ASC")
    suspend fun getCalfMeasurements(): List<BodyMeasurement>
    
    @Insert
    suspend fun insertMeasurement(measurement: BodyMeasurement): Long
    
    @Update
    suspend fun updateMeasurement(measurement: BodyMeasurement)
    
    @Delete
    suspend fun deleteMeasurement(measurement: BodyMeasurement)
    
    @Query("SELECT * FROM body_measurements WHERE date = (SELECT MAX(date) FROM body_measurements)")
    suspend fun getLatestMeasurement(): BodyMeasurement?
}