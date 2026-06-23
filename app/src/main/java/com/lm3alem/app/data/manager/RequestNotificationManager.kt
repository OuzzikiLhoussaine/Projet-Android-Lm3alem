package com.lm3alem.app.data.manager

import android.content.Context
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lm3alem.app.R
import com.lm3alem.app.data.model.RequestStatus
import com.lm3alem.app.data.model.ServiceRequest
import com.lm3alem.app.data.model.UserRole
import com.lm3alem.app.utils.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestNotificationManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificationHelper: NotificationHelper,
    @param:ApplicationContext private val context: Context
) {
    private var artisanListener: ListenerRegistration? = null
    private var clientListener: ListenerRegistration? = null

    fun startListening(userId: String, role: UserRole) {
        stopListening()
        
        if (role == UserRole.ARTISAN) {
            listenForArtisanRequests(userId)
        } else if (role == UserRole.CLIENT) {
            listenForClientUpdates(userId)
        }
    }

    private fun listenForArtisanRequests(artisanId: String) {
        var isInitial = true
        artisanListener = firestore.collection("requests")
            .whereEqualTo("artisanId", artisanId)
            .addSnapshotListener { snapshots, e ->
                if (e != null || (snapshots == null)) return@addSnapshotListener

                if (!isInitial) {
                    for (dc in snapshots.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val request = dc.document.toObject(ServiceRequest::class.java)
                            if (request.status == RequestStatus.PENDING) {
                                // Mark as unread for artisan
                                firestore.collection("requests").document(dc.document.id)
                                    .update("readByArtisan", false)

                                notificationHelper.showNotification(
                                    context.getString(R.string.notification_new_request_title),
                                    context.getString(R.string.notification_new_request_body, request.serviceName)
                                )
                            }
                        }
                    }
                }
                isInitial = false
            }
    }

    private fun listenForClientUpdates(clientId: String) {
        var isInitial = true
        clientListener = firestore.collection("requests")
            .whereEqualTo("clientId", clientId)
            .addSnapshotListener { snapshots, e ->
                if (e != null || (snapshots == null)) return@addSnapshotListener

                if (!isInitial) {
                    for (dc in snapshots.documentChanges) {
                        if (dc.type == DocumentChange.Type.MODIFIED) {
                            val request = dc.document.toObject(ServiceRequest::class.java)
                            val status = request.status
                            
                            val title = context.getString(R.string.notification_request_update_title)
                            val message = when (status) {
                                RequestStatus.ACCEPTED -> context.getString(R.string.notification_request_accepted, request.serviceName)
                                RequestStatus.REFUSED -> context.getString(R.string.notification_request_refused, request.serviceName)
                                else -> null
                            }
                            
                            if (message != null) {
                                // Mark as unread for client
                                firestore.collection("requests").document(dc.document.id)
                                    .update("readByClient", false)

                                notificationHelper.showNotification(title, message)
                            }
                        }
                    }
                }
                isInitial = false
            }
    }

    fun stopListening() {
        artisanListener?.remove()
        clientListener?.remove()
        artisanListener = null
        clientListener = null
    }
}
