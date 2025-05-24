package com.localmarket.app.ui.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.localmarket.app.data.model.Product
import com.localmarket.app.databinding.ItemVendorProductBinding
import com.localmarket.app.utils.formatPrice
import com.localmarket.app.utils.loadImage

class VendorProductAdapter(
    private val onEditClicked: (Product) -> Unit,
    private val onDeleteClicked: (Product) -> Unit
) : ListAdapter<Product, VendorProductAdapter.VendorProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorProductViewHolder {
        val binding = ItemVendorProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VendorProductViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: VendorProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class VendorProductViewHolder(private val binding: ItemVendorProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.buttonEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClicked(getItem(position))
                }
            }
            
            binding.imageViewDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClicked(getItem(position))
                }
            }
        }
        
        fun bind(product: Product) {
            binding.apply {
                textViewProductName.text = product.name
                textViewProductPrice.text = product.price.formatPrice()
                textViewProductStock.text = "Stock: ${product.stock}"
                
                // Load product image
                imageViewProduct.loadImage(product.imageUrl)
            }
        }
    }
    
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}