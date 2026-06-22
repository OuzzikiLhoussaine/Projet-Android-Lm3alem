package com.lm3alem.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lm3alem.app.data.model.ChatRoom
import com.lm3alem.app.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getChatRooms(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val subscription = firestore.collection("chatRooms")
            .whereArrayContains("participantIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { it.toObject(ChatRoom::class.java)?.copy(id = it.id) } ?: emptyList()
                // Sort in memory to avoid needing a composite index in Firestore
                val sortedRooms = rooms.sortedByDescending { it.lastMessageTimestamp }
                trySend(sortedRooms)
            }
        awaitClose { subscription.remove() }
    }

    fun getMessages(chatRoomId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendMessage(chatRoomId: String, message: Message) {
        val messageRef = firestore.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .document()
        
        val newMessage = message.copy(id = messageRef.id)
        
        firestore.runBatch { batch ->
            batch.set(messageRef, newMessage)
            batch.update(
                firestore.collection("chatRooms").document(chatRoomId),
                mapOf(
                    "lastMessage" to message.text,
                    "lastMessageTimestamp" to message.timestamp
                )
            )
        }.await()
    }

    suspend fun getOrCreateChatRoom(participantIds: List<String>): String {
        val sortedIds = participantIds.sorted()
        val existingRoom = firestore.collection("chatRooms")
            .whereEqualTo("participantIds", sortedIds)
            .get()
            .await()

        if (!existingRoom.isEmpty) {
            return existingRoom.documents.first().id
        }

        val newRoomRef = firestore.collection("chatRooms").document()
        val newRoom = ChatRoom(
            id = newRoomRef.id,
            participantIds = sortedIds
        )
        newRoomRef.set(newRoom).await()
        return newRoomRef.id
    }
}
