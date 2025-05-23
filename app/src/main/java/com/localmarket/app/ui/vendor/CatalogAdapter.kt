package com.localmarket.app.ui.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.localmarket.app.data.model.CatalogItem
import com.localmarket.app.databinding.ItemCatalogBinding
import com.localmarket.app.utils.loadImage

class CatalogAdapter(
    private val onItemClicked: (CatalogItem) -> Unit,
    private val onAddClicked: (CatalogItem) -> Unit
) : ListAdapter<CatalogItem, CatalogAdapter.CatalogViewHolder>(CatalogDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val binding = ItemCatalogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CatalogViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class CatalogViewHolder(private val binding: ItemCatalogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position))
                }
            }
            
            binding.buttonAdd.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAddClicked(getItem(position))
                }
            }
        }
        
        fun bind(item: CatalogItem) {
            binding.apply {
                textViewCatalogItemName.text = item.name
                textViewCatalogItemCategory.text = item.category
                
                // Load catalog item image
                imageViewCatalogItem.loadImage(item.imageUrl)
            }
        }
    }
    
    class CatalogDiffCallback : DiffUtil.ItemCallback<CatalogItem>() {
        override fun areItemsTheSame(oldItem: CatalogItem, newItem: CatalogItem): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: CatalogItem, newItem: CatalogItem): Boolean {
            return oldItem == newItem
        }
    }
}