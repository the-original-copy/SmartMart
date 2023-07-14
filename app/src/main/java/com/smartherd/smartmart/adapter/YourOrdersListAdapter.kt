package com.smartherd.smartmart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.MyOrdersItemBinding
import com.smartherd.smartmart.models.OrderedProduct
import com.smartherd.smartmart.models.Product


class YourOrdersListAdapter(private val context: Context, private var list: ArrayList<OrderedProduct>): RecyclerView.Adapter<YourOrdersListAdapter.OrderedProductsViewHolder>() {

    private var onClickListener: OnClickListener? = null
    inner class OrderedProductsViewHolder(itemBinding:MyOrdersItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        var ivProductImage = itemBinding.ivProductImage
        var tvProductName = itemBinding.tvProductName
        var tvProductQuantity = itemBinding.tvProductQuantity
        var tvTotalAmount = itemBinding.tvTotalAmount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedProductsViewHolder {
       return OrderedProductsViewHolder(MyOrdersItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: OrderedProductsViewHolder, position: Int) {
        val product = list[position]
        Glide
            .with(context)
            .load(product.productImage)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.ivProductImage)
        holder.tvProductName.text = "Product name: ${product.productName}"
        holder.tvTotalAmount.text = "Total Price: ${product.totalPrice}"
        holder.tvProductQuantity.text = "Quantity : ${product.quantity}"
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