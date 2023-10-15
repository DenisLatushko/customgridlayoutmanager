package com.example.gridlayoutmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gridlayoutmanager.data.DataProvider
import com.example.gridlayoutmanager.databinding.ActivityMainBinding

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
            layoutManager = GridLayoutManager(this@MainActivity, 5, GridLayoutManager.HORIZONTAL, false)
            adapter = ItemsAdapter().also { it.submitList(DataProvider.buildData()) }
        }
    }
}