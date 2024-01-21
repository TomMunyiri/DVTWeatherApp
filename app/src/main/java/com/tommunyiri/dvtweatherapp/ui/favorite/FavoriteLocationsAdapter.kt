package com.tommunyiri.dvtweatherapp.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.databinding.ItemFavoriteLocationBinding

/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class FavoriteLocationsAdapter(private val delegate: OnItemClickedListener) : ListAdapter<FavoriteLocation, FavoriteLocationsAdapter.ViewHolder>(FavoriteLocationDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchResult = getItem(position)
        if (searchResult != null) {
            holder.itemView.setOnClickListener {
                delegate.onSearchResultClicked(searchResult)
            }
            holder.bind(searchResult)
        }
    }

    class ViewHolder(private val binding: ItemFavoriteLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteLocation: FavoriteLocation) {
            binding.favoriteLocation = favoriteLocation
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFavoriteLocationBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class FavoriteLocationDiffCallBack : DiffUtil.ItemCallback<FavoriteLocation>() {
        override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickedListener {
        fun onSearchResultClicked(dbFavoriteLocation: FavoriteLocation)
    }
}