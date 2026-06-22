package com.lm3alem.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.lm3alem.app.data.model.ChatRoom
import com.lm3alem.app.data.model.Message
import com.lm3alem.app.data.repository.AuthRepository
import com.lm3alem.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    val currentUserId: String? get() = authRepository.currentUser?.uid

    init {
        currentUserId?.let { uid ->
            viewModelScope.launch {
                chatRepository.getChatRooms(uid).collect {
                    _chatRooms.value = it
                }
            }
        }
    }

    fun loadMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId).collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(chatRoomId: String, text: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            val message = Message(
                senderId = uid,
                text = text,
                timestamp = Timestamp.now()
            )
            chatRepository.sendMessage(chatRoomId, message)
        }
    }

    suspend fun getOrCreateChatRoom(otherUserId: String): String {
        val uid = currentUserId ?: throw Exception("User not logged in")
        return chatRepository.getOrCreateChatRoom(listOf(uid, otherUserId))
    }
    
    fun getOtherParticipantId(chatRoom: ChatRoom): String? {
        return chatRoom.participantIds.find { it != currentUserId }
    }
}
