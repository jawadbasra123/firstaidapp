package com.firstaidnow.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firstaidnow.R
import com.firstaidnow.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var topicAdapter: TopicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryGrid()
        setupTopicList()
        setupSearch()
        setupEmergencyButton()
        setupAboutButton()
    }

    private fun setupCategoryGrid() {
        val categories = listOf(
            HomeViewModel.CategoryItem("Burns", R.drawable.ic_burns, R.color.category_burns),
            HomeViewModel.CategoryItem("Bleeding", R.drawable.ic_bleeding, R.color.category_bleeding),
            HomeViewModel.CategoryItem("Choking", R.drawable.ic_choking, R.color.category_choking),
            HomeViewModel.CategoryItem("CPR", R.drawable.ic_cpr, R.color.category_cpr),
            HomeViewModel.CategoryItem("Fractures", R.drawable.ic_fractures, R.color.category_fractures),
            HomeViewModel.CategoryItem("Poisoning", R.drawable.ic_poisoning, R.color.category_poisoning)
        )

        categoryAdapter = CategoryAdapter(categories) { category ->
            val bundle = Bundle().apply { putString("category", category.name) }
            findNavController().navigate(R.id.action_home_to_detail, bundle)
        }

        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = categoryAdapter
        }
    }

    private fun setupTopicList() {
        topicAdapter = TopicAdapter { topic ->
            val bundle = Bundle().apply { putInt("topicId", topic.id) }
            findNavController().navigate(R.id.action_home_to_detail, bundle)
        }

        binding.rvTopics.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topicAdapter
        }

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
            topicAdapter.submitList(topics)
            binding.rvTopics.visibility = if (topics.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.tvNoResults.visibility = if (binding.searchView.query.isNotEmpty() && topics.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText.orEmpty())
                binding.rvCategories.visibility = if (newText.isNullOrEmpty()) View.VISIBLE else View.GONE
                binding.tvCategoriesHeader.visibility = if (newText.isNullOrEmpty()) View.VISIBLE else View.GONE
                return true
            }
        })
    }

    private fun setupEmergencyButton() {
        binding.btnEmergency.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:911")
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:911")))
            } else {
                startActivity(intent)
            }
        }
    }

    private fun setupAboutButton() {
        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_about)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
