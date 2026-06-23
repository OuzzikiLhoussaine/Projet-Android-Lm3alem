package com.lm3alem.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.data.model.ArtisanWithUser
import com.lm3alem.app.data.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getAllArtisans(): Result<List<ArtisanWithUser>> = coroutineScope {
        try {
            val snapshot = firestore.collection("artisans").get().await()
            val artisans = snapshot.toObjects(ArtisanProfile::class.java)
            
            val deferredArtisansWithUsers = artisans.map { artisan ->
                async {
                    val userDoc = firestore.collection("users").document(artisan.userId).get().await()
                    val user = userDoc.toObject(User::class.java)
                    if (user != null) {
                        ArtisanWithUser(artisan, user)
                    } else {
                        null
                    }
                }
            }
            Result.success(deferredArtisansWithUsers.awaitAll().filterNotNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
