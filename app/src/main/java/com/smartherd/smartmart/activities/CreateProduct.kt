package com.smartherd.smartmart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.smartmart.R
import com.smartherd.smartmart.databinding.ActivityCreateProductBinding
import com.smartherd.smartmart.dialogs.LabelCategoryDialog

class CreateProduct : AppCompatActivity() {
    lateinit var binding: ActivityCreateProductBinding
    private var selectedCategory: String = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.etProductCategory.setOnClickListener {
            labelCategoriesListDialog()
        }
    }
    private fun setCategory(category: String) {
        binding.etProductCategory.setText(category)
    }
    private fun labelCategoriesListDialog() {
        val listDialog = object : LabelCategoryDialog(this) {
            override fun onCategorySelected(category: String) {
                selectedCategory = category
                setCategory(selectedCategory)
            }
        }
        listDialog.show()
    }
}