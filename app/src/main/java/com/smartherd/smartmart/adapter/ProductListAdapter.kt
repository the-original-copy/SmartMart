package com.smartherd.smartmart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ProductItemBinding
import com.smartherd.smartmart.models.Product

class ProductListAdapter(private val context: Context,private var list: ArrayList<Product>) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    private var onClickListener: OnClickListener? = null
    inner class ProductViewHolder(itemBinding: ProductItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var ivProductImage = itemBinding.ivProductImage
        var tvProductName = itemBinding.tvProductName
        var tvProductDescription = itemBinding.tvProductDescription
        var tvProductPrice = itemBinding.tvProductPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = list[position]
        Glide
            .with(context)
            .load(product.productImage)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.ivProductImage)
        holder.tvProductName.text = product.productName
        holder.tvProductDescription.text = product.productDescription
        holder.tvProductPrice.text = product.productPrice.toString()
        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position,product)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }
}