package tv.ridal.UI.Layout

import android.content.Context
import android.widget.LinearLayout

open class VLinearLayout(context: Context) : LinearLayout(context)
{
    init
    {
        orientation = LinearLayout.VERTICAL
    }
}