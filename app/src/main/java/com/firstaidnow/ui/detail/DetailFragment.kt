package com.firstaidnow.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firstaidnow.databinding.FragmentDetailBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val topicId = arguments?.getInt("topicId", -1) ?: -1
        val category = arguments?.getString("category")

        if (topicId > 0) {
            observeTopic(topicId)
        } else if (category != null) {
            observeCategory(category)
        }
    }

    private fun observeTopic(topicId: Int) {
        viewModel.getTopicById(topicId).observe(viewLifecycleOwner) { topic ->
            topic?.let {
                binding.tvDetailTitle.text = it.title
                binding.tvDetailCategory.text = it.category
                binding.tvDetailSummary.text = it.summary
                binding.tvSeverityBadge.text = it.severity.uppercase()

                val steps = parseJsonArray(it.steps)
                val warnings = parseJsonArray(it.warnings)

                setupSteps(steps)
                setupWarnings(warnings)

                binding.rvCategoryTopics.visibility = View.GONE
                binding.contentDetail.visibility = View.VISIBLE
            }
        }
    }

    private fun observeCategory(category: String) {
        binding.tvDetailTitle.text = category
        binding.tvDetailCategory.text = "Select a topic"
        binding.contentDetail.visibility = View.GONE
        binding.rvCategoryTopics.visibility = View.VISIBLE

        val adapter = CategoryTopicAdapter { topic ->
            binding.tvDetailTitle.text = topic.title
            binding.tvDetailCategory.text = topic.category
            binding.tvDetailSummary.text = topic.summary
            binding.tvSeverityBadge.text = topic.severity.uppercase()

            val steps = parseJsonArray(topic.steps)
            val warnings = parseJsonArray(topic.warnings)

            setupSteps(steps)
            setupWarnings(warnings)

            binding.rvCategoryTopics.visibility = View.GONE
            binding.contentDetail.visibility = View.VISIBLE
        }

        binding.rvCategoryTopics.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategoryTopics.adapter = adapter

        viewModel.getTopicsByCategory(category).observe(viewLifecycleOwner) { topics ->
            adapter.submitList(topics)
            if (topics.size == 1) {
                // Auto-open if only one topic in category
                adapter.currentList.firstOrNull()?.let { topic ->
                    observeTopic(topic.id)
                }
            }
        }
    }

    private fun setupSteps(steps: List<String>) {
        val adapter = StepAdapter(steps)
        binding.rvSteps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSteps.adapter = adapter
        binding.rvSteps.isNestedScrollingEnabled = false
    }

    private fun setupWarnings(warnings: List<String>) {
        if (warnings.isEmpty()) {
            binding.tvWarningsHeader.visibility = View.GONE
            binding.rvWarnings.visibility = View.GONE
            return
        }
        binding.tvWarningsHeader.visibility = View.VISIBLE
        binding.rvWarnings.visibility = View.VISIBLE
        val adapter = WarningAdapter(warnings)
        binding.rvWarnings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWarnings.adapter = adapter
        binding.rvWarnings.isNestedScrollingEnabled = false
    }

    private fun parseJsonArray(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
