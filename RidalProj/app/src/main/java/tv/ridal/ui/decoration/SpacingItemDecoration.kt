package tv.ridal.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spacing: Int,
    private val topSpacing: Int,
    private val bottomSpacing: Int
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

        outRect.left = spacing
        if (position == itemCount - 1) {
            outRect.right = spacing
        }

        outRect.top = topSpacing
        outRect.bottom = bottomSpacing
    }
}





































//