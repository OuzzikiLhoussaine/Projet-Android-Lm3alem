package com.lm3alem.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lm3alem.app.data.model.ArtisanProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllArtisans(): Result<List<ArtisanProfile>> {
        return try {
            val snapshot = firestore.collection("artisans").get().await()
            val artisans = snapshot.toObjects(ArtisanProfile::class.java)
            Result.success(artisans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
