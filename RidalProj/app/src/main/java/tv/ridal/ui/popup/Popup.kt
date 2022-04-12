package tv.ridal.ui.popup

import android.app.Dialog
import android.content.Context

open class Popup(context: Context) : Dialog(context)
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
            decorView.setBackgroundResource(android.R.color.transparent)
        }
    }
}





































//