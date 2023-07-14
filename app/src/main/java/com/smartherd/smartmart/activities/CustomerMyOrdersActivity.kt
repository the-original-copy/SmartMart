package com.smartherd.smartmart.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.adapter.YourOrdersListAdapter
import com.smartherd.smartmart.databinding.ActivityCustomerMyOrdersBinding
import com.smartherd.smartmart.databinding.DialogProgressBinding
import com.smartherd.smartmart.firebase.FireBaseClass
import com.smartherd.smartmart.models.OrderedProduct
import com.smartherd.smartmart.models.Product

class CustomerMyOrdersActivity : BaseActivity() {
    lateinit var binding: ActivityCustomerMyOrdersBinding
    lateinit var basebinding : DialogProgressBinding
    private lateinit var mProgressDialog: Dialog
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setActionBar(binding.toolbarMyOrdersListActivity,"w")
        getOrderedProducts(getCurrentUserId())
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        basebinding = DialogProgressBinding.inflate(layoutInflater)
        basebinding.root.let {
            mProgressDialog.setContentView(it)
        }
        basebinding.tvProgressText.text = text
        mProgressDialog.show()
    }
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getOrderedProducts(customerID: String) {
        showProgressDialog("Fetching products....")
        FireBaseClass().getProductsOrdered(this,customerID)
    }

    fun assignThisOrderList(hashMap: HashMap<String,OrderedProduct>) {
        hideProgressDialog()
        val orderedProducts: ArrayList<OrderedProduct> = ArrayList(hashMap.values)
        if(orderedProducts.size > 0) {
            binding.rvOrdersList.visibility = View.VISIBLE
            binding.tvNoOrderCreated.visibility = View.GONE
            binding.rvOrdersList.layoutManager = LinearLayoutManager(this)
            binding.rvOrdersList.setHasFixedSize(false)

            val adapter = YourOrdersListAdapter(this,orderedProducts)
            binding.rvOrdersList.adapter = adapter

            adapter.setOnClickListener(object: YourOrdersListAdapter.OnClickListener{
                override fun onClick(position: Int, product: Product) {
                    val arrayList: ArrayList<OrderedProduct> = ArrayList()
                    val builder = AlertDialog.Builder(this@CustomerMyOrdersActivity)
                    builder.setTitle("Your Order")
                    builder.setMessage("Do you want to cancel your order?")
                    builder.setPositiveButton("Cancel Order") { _,_ ->
                        // Cancel Order method
                        FireBaseClass().deleteOrder(this@CustomerMyOrdersActivity,YourOrdersListAdapter(this@CustomerMyOrdersActivity,arrayList).getOrderID())
                    }
                    builder.setNegativeButton("No"){
                        dialog,_ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            })
        } else {
            binding.rvOrdersList.visibility = View.INVISIBLE
            binding.tvNoOrderCreated.visibility = View.VISIBLE
        }
    }

    fun deleteProductSuccess() {
        getOrderedProducts(getCurrentUserId())
    }
}