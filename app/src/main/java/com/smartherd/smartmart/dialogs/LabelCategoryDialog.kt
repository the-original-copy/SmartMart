package com.smartherd.smartmart.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.smartherd.smartmart.databinding.DialogCategoriesBinding
import com.smartherd.smartmart.utils.Constants

abstract class LabelCategoryDialog(context: Context) : Dialog(context) {
    lateinit var binding: DialogCategoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)

        binding.tvCereals.setOnClickListener {
            dismiss()
            onCategorySelected(Constants.CEREALS)
        }
        binding.tvDairy.setOnClickListener {
            dismiss()
            onCategorySelected(Constants.DAIRY)
        }
        binding.tvMeat.setOnClickListener {
            dismiss()
            onCategorySelected(Constants.MEAT)
        }
        binding.tvGreensVegetables.setOnClickListener {
            dismiss()
            onCategorySelected(Constants.GREENSANDVEGETABLES)
        }
        binding.tvOthers.setOnClickListener {
            dismiss()
            onCategorySelected(Constants.OTHER)
        }
    }

    abstract fun onCategorySelected(category: String)
}