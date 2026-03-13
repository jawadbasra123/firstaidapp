package com.firstaidnow.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firstaidnow.FirstAidNowApp
import com.firstaidnow.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    // Use factory to create ViewModel with repository
    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.Factory((requireActivity().application as FirstAidNowApp).repository)
    }

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter()
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvMessages.smoothScrollToPosition(messages.size - 1)
                binding.tvEmptyChat.visibility = View.GONE
            } else {
                binding.tvEmptyChat.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !isLoading
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }

        binding.btnClearChat.setOnClickListener {
            viewModel.clearChat()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
