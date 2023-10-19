package com.example.gridlayoutmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gridlayoutmanager.data.DataProvider
import com.example.gridlayoutmanager.databinding.ActivityMainBinding
import com.example.gridlayoutmanager.gridlayout.CustomGridLayoutManager

private const val ROWS_NUMBER = 2
private const val COLUMNS_NUMBER = 5
private const val SMOOTH_SCROLL_TO = 16

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            viewSmoothScrollButton.apply {
                text = getString(R.string.scroll_to_text, SMOOTH_SCROLL_TO + 1)
                setOnClickListener { viewItemsList.smoothScrollToPosition(SMOOTH_SCROLL_TO) }
            }

            viewItemsList.apply {
                layoutManager = CustomGridLayoutManager(this@MainActivity, ROWS_NUMBER, COLUMNS_NUMBER, false)
                adapter = ItemsAdapter().also { it.submitList(DataProvider.buildData()) }
            }
        }
    }
}