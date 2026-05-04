package com.lm3alem.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lm3alem.app.data.model.ArtisanProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtisanRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveArtisanProfile(profile: ArtisanProfile): Result<Unit> {
        return try {
            firestore.collection("artisans").document(profile.userId).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getArtisanProfile(userId: String): ArtisanProfile? {
        return try {
            val document = firestore.collection("artisans").document(userId).get().await()
            document.toObject(ArtisanProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
