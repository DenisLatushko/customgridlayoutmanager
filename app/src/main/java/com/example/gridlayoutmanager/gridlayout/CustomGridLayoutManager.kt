package com.example.gridlayoutmanager.gridlayout

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class CustomGridLayoutManager(
    context: Context,
    private val rowNumber: Int,
    override val colNumber: Int,
    reverseLayout: Boolean
) : LinearLayoutManager(context, HORIZONTAL, reverseLayout), CustomGridLayoutSnapDataProvider {

    private val maxItemsPerPage: Int = colNumber * rowNumber

    private val pagesNumber: Int
        get() = ceil(itemCount.toDouble() / maxItemsPerPage).toInt()

    private val allColumnsNumber: Int
        get() = colNumber * pagesNumber

    private val horizontalViewPort: Int
        get() = width - paddingStart - paddingEnd

    private val verticalViewPort: Int
        get() = height - paddingTop - paddingBottom

    override val columnItemWidth: Int
        get() = horizontalViewPort / colNumber

    private val rowItemHeight: Int
        get() = verticalViewPort / rowNumber

    private var anchorPosition: Int = 0

    override val currentPageNumber: Int
        get() = anchorPosition / maxItemsPerPage + 1

    override fun getNextPageFirstPosition(isNext: Boolean): Int {
        return if (isNext && currentPageNumber < (itemCount / maxItemsPerPage) + 1) {
            (currentPageNumber - 1) * maxItemsPerPage
        } else if (!isNext && currentPageNumber > 1) {
            (currentPageNumber - 1) * maxItemsPerPage
        } else {
            anchorPosition
        }
    }

    override fun getPageNumberForView(targetView: View): Int =
        getPosition(targetView) / maxItemsPerPage + 1

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply(this::tuneLayoutParams)

    override fun generateLayoutParams(
        c: Context?,
        attrs: AttributeSet?
    ): RecyclerView.LayoutParams =
        super.generateLayoutParams(c, attrs).apply(this::tuneLayoutParams)

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams =
        super.generateLayoutParams(lp).apply(this::tuneLayoutParams)

    private fun tuneLayoutParams(layoutParams: RecyclerView.LayoutParams) {
        layoutParams.apply {
            width = columnItemWidth
            height = rowItemHeight
        }
    }

    override fun getRowCountForAccessibility(recycler: Recycler, state: State): Int =
        if (itemCount > 0) rowNumber else 0

    override fun getColumnCountForAccessibility(recycler: Recycler, state: State): Int =
        if (itemCount > 0) colNumber else 0

    override fun onAdapterChanged(
        oAdapter: RecyclerView.Adapter<*>?,
        nAdapter: RecyclerView.Adapter<*>?
    ) {
        removeAllViews()
    }

    override fun onLayoutChildren(recycler: Recycler, state: State) {
        detachAndScrapAttachedViews(recycler)
        fill(recycler)
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        if (!hasItems()) return 0

        val topLeftView = getChildAt(0) as View
        val topRightView = getChildAt(colNumber - 1) as View

        val isScrollingLeft = isScrollingLeft(dx)
        val isLastColumnVisible = isLastColumnVisible()
        val isFirstColumnVisible = isFirstColumnVisible()

        val offset = when {
            isScrollingLeft && isLastColumnVisible -> max(-dx, horizontalViewPort - getDecoratedRight(topRightView) + paddingRight)
            !isScrollingLeft && isFirstColumnVisible -> min(-dx, -getDecoratedLeft(topLeftView) + paddingLeft)
            else -> -dx
        }

        offsetChildrenHorizontal(offset)

        if (isScrollingLeft) {
            if (getDecoratedRight(topLeftView) < 0 && !isLastColumnVisible) {
                moveAnchorPosition(ScrollDirection.LEFT)
                fill(recycler)
            } else if (!isLastColumnVisible) {
                fill(recycler)
            }
        } else {
            if (getDecoratedLeft(topRightView) > 0 && !isFirstColumnVisible) {
                moveAnchorPosition(ScrollDirection.RIGHT)
                fill(recycler)
            } else if (!isFirstColumnVisible) {
                fill(recycler)
            }
        }

        return -offset
    }

    private fun moveAnchorPosition(direction: ScrollDirection) {
        val maxPageNumber = itemCount / maxItemsPerPage + 1
        val pageNumber = anchorPosition / maxItemsPerPage + 1

        if (direction == ScrollDirection.RIGHT) {
            if (pageNumber == 1) {
                val nexAnchorPosition = anchorPosition - 1
                anchorPosition = if (nexAnchorPosition > 0) nexAnchorPosition else 0
            } else {
                val latItemNumber = pageNumber * maxItemsPerPage
                anchorPosition -= if (anchorPosition == latItemNumber) colNumber else 1
            }
        } else if (direction == ScrollDirection.LEFT) {
            val isPageFull = pageNumber < maxPageNumber || maxPageNumber * maxItemsPerPage == itemCount

            val lastAnchorPositionOnPage = if (isPageFull) {
                pageNumber * maxItemsPerPage - colNumber - 1
            } else {
                val missedItemsNumber = maxPageNumber * maxItemsPerPage - itemCount
                val itemsNumberToCut = missedItemsNumber * rowNumber
                itemCount - itemsNumberToCut - 1
            }

            if (pageNumber != maxPageNumber) {
                if (anchorPosition == lastAnchorPositionOnPage) {
                    anchorPosition += colNumber
                }
                anchorPosition++
            } else {
                val nexAnchorPosition = anchorPosition + 1
                anchorPosition = if (nexAnchorPosition < lastAnchorPositionOnPage) nexAnchorPosition else lastAnchorPositionOnPage
            }
        }
    }

    private fun hasItems(): Boolean = itemCount > 0

    private fun isScrollingLeft(dx: Int): Boolean = dx > 0

    private fun isFirstColumnVisible(): Boolean = (anchorPosition % allColumnsNumber) == 0

    private fun isLastColumnVisible(): Boolean = ((anchorPosition % allColumnsNumber) + colNumber) >= allColumnsNumber

    private fun fill(recycler: Recycler) {
        if (itemCount <= 0) return

        val viewCache = cacheExistingViews()
        detachCachedViews(viewCache)
        fillVisibleGrid(recycler, viewCache)
        recycleNotUsedViews(recycler, viewCache)
    }

    private fun cacheExistingViews(): SparseArray<View> = SparseArray<View>(childCount).apply {
        (0 until (childCount)).forEach { itemIndex ->
            val viewPosition = mapViewPositionToRecyclerViewIndex(itemIndex)
            getChildAt(viewPosition)?.run { put(viewPosition, this) }
        }
    }

    private fun recycleNotUsedViews(recycler: Recycler, viewCache: SparseArray<View>) {
        viewCache.forEach { _, view ->  recycler.recycleView(view) }
        viewCache.clear()
    }

    private fun detachCachedViews(viewCache: SparseArray<View>) {
        viewCache.forEach { _, view ->  detachView(view)}
    }

    private fun fillVisibleGrid(recycler: Recycler, viewCache: SparseArray<View>) {
        var xOffsetInd = 0
        var yOffsetInd = 0
        var currentPage = anchorPosition / colNumber

        (0 until  maxItemsPerPage).forEach { visibleItemIndex ->
            val visibleViewIndex = mapViewPositionToRecyclerViewIndex(visibleItemIndex)

            if (visibleViewIndex < itemCount) {
                val pageNumber = visibleViewIndex / maxItemsPerPage

                if (pageNumber != currentPage) {
                    currentPage = pageNumber
                    xOffsetInd = pageNumber * colNumber
                    yOffsetInd = 0
                }

                val view = viewCache.get(visibleViewIndex)
                if (view != null) {
                    attachView(view)
                    viewCache.remove(visibleViewIndex)
                } else {
                    val childView = recycler.getViewForPosition(visibleViewIndex)
                    addView(childView)
                    measureChild(childView, 0, 0)

                    val left = xOffsetInd * columnItemWidth
                    val right = left + columnItemWidth
                    val top = yOffsetInd * rowItemHeight
                    val bottom = top + rowItemHeight

                    layoutDecorated(childView, left, top, right, bottom)
                }

                val maxColNumberPerPage = (pageNumber + 1) * colNumber - 1
                if (xOffsetInd < maxColNumberPerPage) {
                    xOffsetInd++
                } else {
                    xOffsetInd = pageNumber * colNumber
                    yOffsetInd = if (yOffsetInd < (rowNumber - 1)) yOffsetInd + 1 else 0
                }
            } else {
                return
            }
        }
    }

    private fun mapViewPositionToRecyclerViewIndex(visibleViewPosition: Int): Int {
        val row = visibleViewPosition / colNumber
        val column = visibleViewPosition % colNumber
        return row * colNumber + column + anchorPosition
    }

    override fun canScrollVertically(): Boolean = false

    override fun canScrollHorizontally(): Boolean = true

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: State, position: Int) {
        val scroller = object: LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
                val targetPageNumber = targetPosition / maxItemsPerPage
                val tempTargetIndex = targetPosition % maxItemsPerPage
                val colInd = targetPageNumber * colNumber + if (tempTargetIndex < colNumber) tempTargetIndex else tempTargetIndex - colNumber

                val pageNumber = anchorPosition / maxItemsPerPage
                val tempAnchorIndex = anchorPosition % maxItemsPerPage
                val anchorColInd = pageNumber * colNumber + if (tempAnchorIndex < colNumber) tempAnchorIndex else tempAnchorIndex - colNumber

                return PointF((colInd - anchorColInd).toFloat() * columnItemWidth, 0f)
            }
        }

        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }
}

private enum class ScrollDirection {
    LEFT, RIGHT, NONE
}