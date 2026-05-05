package com.lm3alem.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lm3alem.app.data.model.ArtisanProfile
import com.lm3alem.app.data.model.Review
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun addReview(review: Review): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val artisanRef = firestore.collection("artisans").document(review.artisanId)
                val artisanSnapshot = transaction.get(artisanRef)
                val artisan = artisanSnapshot.toObject(ArtisanProfile::class.java) ?: ArtisanProfile(userId = review.artisanId)

                val newReviewCount = artisan.reviewCount + 1
                val newRating = ((artisan.rating * artisan.reviewCount) + review.rating) / newReviewCount

                val reviewRef = firestore.collection("reviews").document()
                val finalReview = review.copy(id = reviewRef.id)

                transaction.set(reviewRef, finalReview)
                transaction.update(artisanRef, "rating", newRating)
                transaction.update(artisanRef, "reviewCount", newReviewCount)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviewsForArtisan(artisanId: String): Result<List<Review>> {
        return try {
            val snapshot = firestore.collection("reviews")
                .whereEqualTo("artisanId", artisanId)
                .get()
                .await()
            val reviews = snapshot.toObjects(Review::class.java)
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
