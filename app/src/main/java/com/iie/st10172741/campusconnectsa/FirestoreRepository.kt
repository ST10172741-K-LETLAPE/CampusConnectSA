package com.iie.st10172741.campusconnectsa

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * NoSQL Database - Firestore
 * Required for OPSC6322 Part 3: Use a NoSQL database to store data / read data
 * Supabase is SQL, so this satisfies NoSQL requirement for Part 3
 */
class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // STORE data in NoSQL
    suspend fun logActivity(userId: String, action: String, eventId: String? = null) {
        val data = hashMapOf(
            "user_id" to userId,
            "action" to action,
            "event_id" to eventId,
            "timestamp" to System.currentTimeMillis(),
            "campus" to "Main"
        )
        db.collection("activity_logs").add(data).await()
    }

    // READ data from NoSQL
    suspend fun getRecentActivity(userId: String): List<Map<String, Any>> {
        val snapshot = db.collection("activity_logs")
            .whereEqualTo("user_id", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()
        return snapshot.documents.map { it.data ?: emptyMap() }
    }

    // For Campus Services real-time updates (innovative)
    suspend fun addCampusAlert(title: String, message: String, type: String) {
        val alert = hashMapOf(
            "title" to title,
            "message" to message,
            "type" to type,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("campus_alerts").add(alert).await()
    }
}


