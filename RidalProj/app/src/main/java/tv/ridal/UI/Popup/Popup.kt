package tv.ridal.UI.Popup

import android.app.Dialog
import android.content.Context
import tv.ridal.R

open class Popup(context: Context) : Dialog(context)
{
    private val DIM: Float = 0.33F

    init
    {
        window?.apply {
            setDimAmount(DIM)
        }
    }
}





































//