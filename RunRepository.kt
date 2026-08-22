package com.example.data.repository

import com.example.data.db.RunSessionDao
import com.example.data.model.RunSession
import kotlinx.coroutines.flow.Flow

class RunRepository(private val dao: RunSessionDao) {
    val allRuns: Flow<List<RunSession>> = dao.getAllRuns()

    suspend fun saveRun(session: RunSession): Long {
        return dao.insertRun(session)
    }

    suspend fun deleteRun(id: Long) {
        dao.deleteRunById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
