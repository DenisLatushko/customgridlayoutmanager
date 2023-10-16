package com.example.gridlayoutmanager.gridlayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State

class CustomGridLayoutManager(
    context: Context,
    private val rowNumber: Int,
    private val colNumber: Int,
    reverseLayout: Boolean
): GridLayoutManager(context, rowNumber, HORIZONTAL, reverseLayout) {

    private val cleanHorizontalSpace: Int
        get() = width - paddingStart - paddingEnd

    private val cleanVerticalSpace: Int
        get() = height - paddingTop - paddingBottom

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        super.generateDefaultLayoutParams().apply(this::tuneLayoutParams)

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?): RecyclerView.LayoutParams =
        super.generateLayoutParams(c, attrs).apply(this::tuneLayoutParams)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams =
        super.generateLayoutParams(lp).apply(this::tuneLayoutParams)

    private fun tuneLayoutParams(layoutParams: RecyclerView.LayoutParams) {
        layoutParams.apply {
            width = cleanHorizontalSpace / colNumber
            height = cleanVerticalSpace / rowNumber
        }
    }

    override fun getRowCountForAccessibility(recycler: Recycler, state: State): Int =
        if (itemCount > 0) rowNumber else 0

    override fun getColumnCountForAccessibility(recycler: Recycler, state: State): Int =
        if (itemCount > 0) colNumber else 0

    override fun canScrollVertically(): Boolean  = false

    override fun canScrollHorizontally(): Boolean  = true
}
