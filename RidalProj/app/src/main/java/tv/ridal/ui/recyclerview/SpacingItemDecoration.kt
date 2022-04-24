package tv.ridal.ui.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spacing: Int,
    private val topSpacing: Int = 0,
    private val bottomSpacing: Int = 0
) : RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter!!.itemCount

        if (position != 0) outRect.left = spacing

        outRect.top = topSpacing
        outRect.bottom = bottomSpacing
    }
}





































//