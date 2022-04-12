package tv.ridal.ui.popup

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import tv.ridal.utils.Theme
import tv.ridal.ui.layout.Layout
import tv.ridal.R
import tv.ridal.utils.Utils

open class BottomPopup(context: Context) : BottomSheetDialog(context, R.style.BottomPopup)
{
    var dim: Float = 0.33F // !
        set(value) {
            field = value

            window?.setDimAmount(dim)
        }

    init
    {
        window?.apply {
            setDimAmount(dim)
        }
    }

    var isDraggable: Boolean
        get() = this.behavior.isDraggable
        set(value) {
            this.behavior.isDraggable = value
        }

    inner class HolderView : FrameLayout(context)
    {
        init
        {
            val holder = Theme.rect(Theme.color_popup_holder, radii = FloatArray(4).apply {
                fill(Utils.dp(4F))
            })
            val holderView = ImageView(context).apply {
                setImageDrawable(holder)
            }
            addView(holderView, Layout.ezFrame(
                32, 4
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
                MeasureSpec.makeMeasureSpec( Utils.dp(22), MeasureSpec.EXACTLY )
            )
        }
    }

}





































//