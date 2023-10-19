package com.example.gridlayoutmanager.gridlayout

import android.view.View

interface CustomGridLayoutSnapDataProvider {
    val colNumber: Int
    val columnItemWidth: Int
    val currentPageNumber: Int
    fun getNextPageFirstPosition(isNext: Boolean): Int
    fun getPageNumberForView(targetView: View): Int
}