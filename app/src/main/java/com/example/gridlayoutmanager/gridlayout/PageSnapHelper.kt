package com.example.gridlayoutmanager.gridlayout

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.SnapHelper
import com.example.gridlayoutmanager.utils.LOG_TAG
import kotlin.math.abs

private fun LayoutManager.toCustomGridLayoutSnapDataProvider(): CustomGridLayoutSnapDataProvider? = this as? CustomGridLayoutSnapDataProvider

class PageSnapHelper: SnapHelper() {

    override fun calculateDistanceToFinalSnap(layoutManager: LayoutManager, targetView: View): IntArray? =
        layoutManager.toCustomGridLayoutSnapDataProvider()
            ?.let { provider ->
                IntArray(2).apply {
                    set(1, 0)

                    val currentPage = provider.currentPageNumber
                    val targetPage = provider.getPageNumberForView(targetView)
                    val pageDistance = abs(currentPage - targetPage)
                    val offset = pageDistance * provider.columnItemWidth

                    if (targetPage > currentPage) {
                        set(0, offset)
                    } else {
                        set(0, -offset)
                    }
                }
            }

    override fun findSnapView(layoutManager: LayoutManager): View? {
        val helper = OrientationHelper.createHorizontalHelper(layoutManager)
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }

        var closestChild: View? = null
        val start: Int = helper.startAfterPadding
        var absClosest = Int.MAX_VALUE

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedStart(child)
            val absDistance = abs(childStart - start)

            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }

        Log.d(LOG_TAG, "findSnapView: ${(closestChild as TextView).text}")

        return closestChild
    }

    override fun findTargetSnapPosition(layoutManager: LayoutManager, velocityX: Int, velocityY: Int): Int =
        layoutManager.toCustomGridLayoutSnapDataProvider()
            ?.let { provider ->
                provider.getNextPageFirstPosition(velocityX > 0).also {
                    Log.d(this@PageSnapHelper.LOG_TAG, "getNextPageFirstPosition: $it")
                }
            }
            ?: 0
}