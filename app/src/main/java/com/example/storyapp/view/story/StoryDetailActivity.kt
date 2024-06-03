package com.example.storyapp.view.story

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.view.utils.ConvertLocation
import com.example.storyapp.view.utils.convertDateTime
import com.example.storyapp.view.utils.lightStatusBar

@Suppress("DEPRECATION")
class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        lightStatusBar(window, true)
        setContentView(binding.root)
        backButton()

        val storyDetail: Parcelable? = intent.getParcelableExtra(EXTRA_DETAIL_STORY)
        if (storyDetail != null) {
            when (storyDetail) {
                is StoryEntity -> showViewModel(storyDetail)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showViewModel(story: StoryEntity) {
        binding.apply {
            tvDetailStory.text = getString(R.string.MSG_DETAIL_STORY) + " ${story.name}"
            tvUsername.text = story.name
            tvCapstion.text = story.description
            tvDate.text = convertDateTime(story.createdAt)
            tvLocation.text = ConvertLocation.getStringAddress(
                ConvertLocation.convertLatLng(story.lat, story.lon),
                this@StoryDetailActivity
            )
        }
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.imgStory)
    }

    private fun backButton() {
        binding.apply {
            bckDetailStory.setOnClickListener {
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_DETAIL_STORY = "extra_detail_story"
        const val EXTRA_DETAIL_STORY_ID = "extra_detail_story_id"
    }
}