package com.lm3alem.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.ServiceRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun sendRequest(request: ServiceRequest): Result<Unit> {
        return try {
            val docRef = firestore.collection("requests").document()
            val newRequest = request.copy(id = docRef.id)
            docRef.set(newRequest).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRequestsForArtisan(artisanId: String): Result<List<ServiceRequest>> {
        return try {
            val snapshot = firestore.collection("requests")
                .whereEqualTo("artisanId", artisanId)
                .get()
                .await()
            val requests = snapshot.toObjects(ServiceRequest::class.java)
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRequestStatus(requestId: String, status: RequestStatus): Result<Unit> {
        return try {
            firestore.collection("requests").document(requestId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
