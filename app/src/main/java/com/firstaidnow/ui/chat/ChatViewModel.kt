package com.firstaidnow.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstaidnow.BuildConfig
import com.firstaidnow.data.remote.Content
import com.firstaidnow.data.remote.GeminiRequest
import com.firstaidnow.data.remote.Part
import com.firstaidnow.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    data class ChatMessage(
        val text: String,
        val isUser: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val conversationHistory = mutableListOf<Content>()

    private val systemPrompt = Content(
        role = "user",
        parts = listOf(
            Part(
                "You are a helpful first aid assistant for the FirstAidNow app. " +
                "Provide clear, concise first aid guidance. Always remind users to call 911 " +
                "for life-threatening emergencies. You are NOT a replacement for professional " +
                "medical advice. Keep responses focused on first aid topics. " +
                "Format your responses with clear steps when giving instructions."
            )
        )
    )

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        val currentMessages = _messages.value.orEmpty().toMutableList()
        currentMessages.add(ChatMessage(userMessage, isUser = true))
        _messages.value = currentMessages

        conversationHistory.add(
            Content(role = "user", parts = listOf(Part(userMessage)))
        )

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val request = GeminiRequest(
                    contents = conversationHistory,
                    systemInstruction = systemPrompt
                )

                val response = RetrofitClient.geminiApi.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )

                val reply = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text
                    ?: "I'm sorry, I couldn't generate a response. Please try again."

                conversationHistory.add(
                    Content(role = "model", parts = listOf(Part(reply)))
                )

                val updated = _messages.value.orEmpty().toMutableList()
                updated.add(ChatMessage(reply, isUser = false))
                _messages.value = updated

            } catch (e: Exception) {
                val updated = _messages.value.orEmpty().toMutableList()
                updated.add(
                    ChatMessage(
                        "Sorry, I couldn't connect to the AI service. " +
                        "Please check your internet connection and API key. Error: ${e.message}",
                        isUser = false
                    )
                )
                _messages.value = updated
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
        conversationHistory.clear()
    }
}
