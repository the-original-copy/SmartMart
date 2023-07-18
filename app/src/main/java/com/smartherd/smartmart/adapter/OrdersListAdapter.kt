package com.smartherd.smartmart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.OrdersItemBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.OrderedProduct
import com.smartherd.smartmart.models.Product


class OrdersListAdapter(private val context: Context, private var list: ArrayList<OrderedProduct>): RecyclerView.Adapter<OrdersListAdapter.OrderedProductsViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var orderedProduct: OrderedProduct? = null
    inner class OrderedProductsViewHolder(itemBinding: OrdersItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        var ivProductImage = itemBinding.ivProductImage
        var tvProductName = itemBinding.tvProductName
        var tvProductQuantity = itemBinding.tvProductQuantity
        var tvTotalAmount = itemBinding.tvTotalAmount
        var tvFrom = itemBinding.tvFrom
        var tvUniqueID = itemBinding.tvProductUniqueShortID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedProductsViewHolder {
       return OrderedProductsViewHolder(OrdersItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: OrderedProductsViewHolder, position: Int) {
        val product = list[position]
        orderedProduct = product
        Glide
            .with(context)
            .load(product.productImage)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.ivProductImage)
        holder.tvProductName.text = "Product name: ${product.productName}"
        holder.tvTotalAmount.text = "Total Price:  ${product.totalPrice}"
        holder.tvProductQuantity.text = "Quantity : ${product.quantity}"
        // From customer account since OrderedProduct should have farmerID
        if(product.customerID == ""){
            holder.tvFrom.text = "Farmer Name: ${orderedProduct!!.farmerName}"
        }
        // From farmer account since OrderedProduct should have customerID
        if(product.farmerID == "") {
            holder.tvFrom.text = "Customer ID: ${orderedProduct!!.customerID}"
            holder.tvUniqueID.text = "Unique Confirmation ID: ${orderedProduct!!.uniqueProductID.toString()}"
        }
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
        fun onClick(position: Int, product: OrderedProduct)
    }

    fun getOrderID() : String {
        return orderedProduct!!.orderID
    }

}