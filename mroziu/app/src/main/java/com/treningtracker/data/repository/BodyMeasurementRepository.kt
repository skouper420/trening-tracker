package com.treningtracker.data.repository

import com.treningtracker.data.dao.BodyMeasurementDao
import com.treningtracker.data.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyMeasurementRepository @Inject constructor(
    private val bodyMeasurementDao: BodyMeasurementDao
) {
    
    fun getAllMeasurements(): Flow<List<BodyMeasurement>> = 
        bodyMeasurementDao.getAllMeasurements()
    
    suspend fun getMeasurementById(id: Long): BodyMeasurement? = 
        bodyMeasurementDao.getMeasurementById(id)
    
    suspend fun getMeasurementsByDateRange(startDate: Long, endDate: Long): List<BodyMeasurement> = 
        bodyMeasurementDao.getMeasurementsByDateRange(startDate, endDate)
    
    suspend fun getWeightMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getWeightMeasurements()
    
    suspend fun getWaistMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getWaistMeasurements()
    
    suspend fun getChestMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getChestMeasurements()
    
    suspend fun getBicepsMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getBicepsMeasurements()
    
    suspend fun getForearmMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getForearmMeasurements()
    
    suspend fun getThighMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getThighMeasurements()
    
    suspend fun getCalfMeasurements(): List<BodyMeasurement> = 
        bodyMeasurementDao.getCalfMeasurements()
    
    suspend fun insertMeasurement(measurement: BodyMeasurement): Long = 
        bodyMeasurementDao.insertMeasurement(measurement)
    
    suspend fun updateMeasurement(measurement: BodyMeasurement) = 
        bodyMeasurementDao.updateMeasurement(measurement)
    
    suspend fun deleteMeasurement(measurement: BodyMeasurement) = 
        bodyMeasurementDao.deleteMeasurement(measurement)
    
    suspend fun getLatestMeasurement(): BodyMeasurement? = 
        bodyMeasurementDao.getLatestMeasurement()
}