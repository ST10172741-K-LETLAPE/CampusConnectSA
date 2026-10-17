package com.iie.st10172741.campusconnectsa

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadEventPoster(imageBytes: ByteArray): String {
        val fileName = "event-posters/event_${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putBytes(imageBytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadStudyResource(fileBytes: ByteArray, fileName: String): String {
        val name = "study-resources/resource_${UUID.randomUUID()}_$fileName"
        val ref = storage.reference.child(name)
        ref.putBytes(fileBytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadProfilePic(userId: String, imageBytes: ByteArray): String {
        val fileName = "profile-pics/profile_$userId.jpg"
        val ref = storage.reference.child(fileName)
        ref.putBytes(imageBytes).await()
        return ref.downloadUrl.await().toString()
    }
}