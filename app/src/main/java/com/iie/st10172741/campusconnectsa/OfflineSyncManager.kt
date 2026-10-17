package com.iie.st10172741.campusconnectsa

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.work.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore

@Entity(tableName = "pending_actions")
data class PendingAction(
    @PrimaryKey val id: String,
    val action: String,
    val data: String,
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class SyncWorker(appContext: android.content.Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val db = FirebaseFirestore.getInstance()
            val pending = inputData.getString("data") ?: return Result.success()
            db.collection("activity_logs").add(mapOf("data" to pending, "timestamp" to System.currentTimeMillis())).await()
            Result.success()
        } catch (e: Exception) { Result.retry() }
    }
}

object OfflineSyncManager {
    fun queueSync(context: android.content.Context, actionId: String, jsonData: String) {
        val data = workDataOf("data" to jsonData, "id" to actionId)
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(data)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}