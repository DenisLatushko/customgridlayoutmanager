package com.example.gridlayoutmanager.data

object DataProvider {
    fun buildData(): List<DataItem> = mutableListOf<DataItem>()
        .apply { repeat(17) { add(DataItem("Item ${it + 1}")) } }
}