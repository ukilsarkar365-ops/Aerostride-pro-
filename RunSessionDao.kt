package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RunSession
import kotlinx.coroutines.flow.Flow

@Dao
interface RunSessionDao {
    @Query("SELECT * FROM run_sessions ORDER BY timestamp DESC")
    fun getAllRuns(): Flow<List<RunSession>>

    @Query("SELECT * FROM run_sessions WHERE id = :id")
    suspend fun getRunById(id: Long): RunSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(session: RunSession): Long

    @Query("DELETE FROM run_sessions WHERE id = :id")
    suspend fun deleteRunById(id: Long)

    @Query("DELETE FROM run_sessions")
    suspend fun clearAll()
}
