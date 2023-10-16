package com.example.gridlayoutmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gridlayoutmanager.data.DataProvider
import com.example.gridlayoutmanager.databinding.ActivityMainBinding
import com.example.gridlayoutmanager.gridlayout.CustomGridLayoutManager

private const val ROWS_NUMBER = 2
private const val COLUMNS_NUMBER = 5

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initList()
    }

    private fun initList() {
        binding.viewItemsList.apply {
            layoutManager = CustomGridLayoutManager(this@MainActivity, ROWS_NUMBER, COLUMNS_NUMBER, false)
            adapter = ItemsAdapter().also { it.submitList(DataProvider.buildData()) }
        }
    }
}