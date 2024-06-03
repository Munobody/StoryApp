package com.example.storyapp.view.story

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.PagingStoryModelFactory
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.databinding.FragmentStoryBinding
import com.example.storyapp.view.login.dataStore
import com.example.storyapp.view.utils.AuthViewModel
import com.example.storyapp.view.utils.PreferenceManager
import com.example.storyapp.view.utils.lightStatusBar

class StoryFragment : Fragment() {
    private lateinit var binding: FragmentStoryBinding
    private lateinit var prefen: PreferenceManager
    private lateinit var token: String
    private val viewModel: StoryViewModel by lazy {
        ViewModelProvider(this, PagingStoryModelFactory(requireContext()))[StoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefen = PreferenceManager.getInstance(requireContext().dataStore)
        lightStatusBar(requireActivity().window, true)
        floatingClick()
        showRecyclerView()

        val authViewModel =
            ViewModelProvider(this, StoryModelFactory(prefen))[AuthViewModel::class.java]
        authViewModel.getUserLoginToken().observe(viewLifecycleOwner) {
            token = it
            setStoryData(it)
        }
    }

    private fun showRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.setHasFixedSize(true)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setStoryData(token: String) {
        val storyAdapter = StoryAdapter()
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            })

        viewModel.getPagingStory(token).observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryEntity) {
                selectedStory(data)
            }
        })
    }

    private fun floatingClick(){
        binding.apply {
            btnFloating.setOnClickListener{
                val intent = Intent(requireContext(), StoryUploadActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun selectedStory(story: StoryEntity) {
        val intent = Intent(requireContext(), StoryDetailActivity::class.java)
        intent.putExtra(StoryDetailActivity.EXTRA_DETAIL_STORY, story)
        intent.putExtra(StoryDetailActivity.EXTRA_DETAIL_STORY_ID, story.id)
        startActivity(intent)
    }
}
