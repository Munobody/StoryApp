package com.example.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.view.story.StoryDetailActivity
import com.example.storyapp.view.utils.convertDateTime
import com.example.storyapp.view.utils.getTimeAgo
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.ListViewHolder>(DiffCallback()) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            item?.let { it1 ->
                val intent = Intent(holder.itemView.context, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.EXTRA_DETAIL_STORY, it1)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as Activity,
                        Pair(holder.binding.imgItemPhoto, "image"),
                    )
                holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    class ListViewHolder(var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryEntity) {
            val dateTime = convertDateTime(story.createdAt)
            val dateTimeMillis = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTime)!!.time
            val username = story.name
            binding.tvItemName.text = username
            binding.tvItemName.setTypeface(null, Typeface.BOLD)
            val captionText = SpannableStringBuilder()
                .append(username, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append(" ${story.description}")
            if (username != null) {
                captionText.setSpan(ForegroundColorSpan(Color.BLACK), 0, username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.tvItemCapstion.text = captionText
            binding.tvItemDate.text = getTimeAgo(dateTimeMillis)

            Glide.with(binding.root)
                .load(story.photoUrl)
                .into(binding.imgItemPhoto)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StoryEntity>() {
        override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StoryEntity,
            newItem: StoryEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

}