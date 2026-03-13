package com.firstaidnow.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.firstaidnow.BuildConfig
import com.firstaidnow.repository.FirstAidRepository
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: FirstAidRepository) : ViewModel() {

    // 现在安全地从 BuildConfig 中读取 Key
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    data class ChatMessage(
        val text: String,
        val isUser: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        val currentMessages = _messages.value.orEmpty().toMutableList()
        currentMessages.add(ChatMessage(userMessage, isUser = true))
        _messages.value = currentMessages

        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d("ChatViewModel", "Sending message to Gemini...")
                val reply = repository.getGeminiResponse(API_KEY, userMessage)
                
                if (reply != null) {
                    val updated = _messages.value.orEmpty().toMutableList()
                    updated.add(ChatMessage(reply, isUser = false))
                    _messages.value = updated
                } else {
                    addErrorMessage("AI returned an empty response. Check your API key in local.properties.")
                }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error: ${e.message}", e)
                addErrorMessage("Connection Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addErrorMessage(error: String) {
        val updated = _messages.value.orEmpty().toMutableList()
        updated.add(ChatMessage("Error: $error", isUser = false))
        _messages.value = updated
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    class Factory(private val repository: FirstAidRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
