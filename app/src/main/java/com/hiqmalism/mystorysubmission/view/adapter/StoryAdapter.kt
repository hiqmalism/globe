package com.hiqmalism.mystorysubmission.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hiqmalism.mystorysubmission.R
import com.hiqmalism.mystorysubmission.data.api.response.ListStoryItem
import com.hiqmalism.mystorysubmission.databinding.ItemStoriesBinding
import com.hiqmalism.mystorysubmission.view.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from
            (parent.context),
            parent,
            false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
        holder.itemView.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.itemViewBinding.ivItemPhoto, "photo"),
                    Pair(holder.itemViewBinding.tvItemName, "name"),
                    Pair(holder.itemViewBinding.tvDate, "date"),
                    Pair(holder.itemViewBinding.tvDescription, "description")
                )
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("id", item.id)
            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    class StoryViewHolder(private val binding: ItemStoriesBinding)
        : RecyclerView.ViewHolder(binding.root) {

        val itemViewBinding: ItemStoriesBinding
            get() = binding

        fun bind(item: ListStoryItem) {
            val context = binding.root.context
            Glide.with(context)
                .load(item.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = item.name
            val createdAtText = context.getString(R.string.created_at, formatDate(item.createdAt.toString()))
            binding.tvDate.text = createdAtText
            binding.tvDescription.text = item.description
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            return date?.let { outputFormat.format(it) }.toString()
        }
    }

    companion object {
        val DIFF_CALLBACK = object  : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}