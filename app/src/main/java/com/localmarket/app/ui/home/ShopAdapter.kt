package com.localmarket.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.localmarket.app.data.model.Shop
import com.localmarket.app.databinding.ItemShopBinding
import com.localmarket.app.utils.loadImage

class ShopAdapter(
    private val onShopClicked: (Shop) -> Unit,
    private val onWhatsAppClicked: (String) -> Unit
) : ListAdapter<Shop, ShopAdapter.ShopViewHolder>(ShopDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShopViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onShopClicked(getItem(position))
                }
            }
            
            binding.buttonWhatsApp.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val shop = getItem(position)
                    shop.whatsappNumber?.let { number ->
                        onWhatsAppClicked(number)
                    }
                }
            }
        }
        
        fun bind(shop: Shop) {
            binding.apply {
                textViewShopName.text = shop.name
                textViewShopAddress.text = shop.address
                
                // Load shop banner image
                imageViewShopBanner.loadImage(shop.bannerImage)
                
                // Set distance if available
                if (shop.distance != null) {
                    textViewDistance.text = shop.distanceFormatted
                } else {
                    textViewDistance.text = ""
                }
                
                // Show/hide WhatsApp button based on availability
                buttonWhatsApp.isEnabled = !shop.whatsappNumber.isNullOrEmpty()
            }
        }
    }
    
    class ShopDiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return oldItem == newItem
        }
    }
}