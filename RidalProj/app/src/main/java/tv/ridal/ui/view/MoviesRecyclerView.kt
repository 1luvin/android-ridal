package tv.ridal.ui.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.EdgeEffect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.utils.Theme
import tv.ridal.utils.Utils

class MoviesRecyclerView(context: Context) : RecyclerView(context)
{
    init
    {
        setPadding(Utils.dp(5), 0, Utils.dp(5), 0)
        clipToPadding = false

        edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
            }
        }

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun setAdapter(adapter: Adapter<*>?)
    {
        super.setAdapter(adapter)

        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                val position = parent.getChildAdapterPosition(view)
                val size = adapter!!.itemCount

                outRect.left = Utils.dp(15)
                outRect.top = Utils.dp(5)

                if (position == size - 1)
                    outRect.right = Utils.dp(15)

                outRect.bottom = Utils.dp(15)
            }
        })
    }
}






































//