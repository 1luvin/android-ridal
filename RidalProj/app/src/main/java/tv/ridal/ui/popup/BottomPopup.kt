package tv.ridal.ui.popup

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import tv.ridal.util.Theme
import tv.ridal.ui.layout.Layout
import tv.ridal.R
import tv.ridal.util.Utils

open class BottomPopup(context: Context) : BottomSheetDialog(context, R.style.BottomPopup)
{
    var dim: Float = 0.33F // !
        set(value) {
            field = value

            window?.setDimAmount(dim)
        }

    init
    {
        window?.setDimAmount(dim)
    }

    var isDraggable: Boolean
        get() = this.behavior.isDraggable
        set(value) {
            this.behavior.isDraggable = value
        }

}





































//