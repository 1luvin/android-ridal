package tv.ridal.Components

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Utils.Utils

class ScreenTitleBar(context: Context) : FrameLayout(context)
{
    private var titleView: TextView

    var title: String = ""
        set(value) {
            field = value

            titleView.text = title
        }

    init
    {
        setPadding(0, Utils.dp(25), 0, 0)

        titleView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 40F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(titleView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            30, 0, 30, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paddingTop + Utils.dp(90), MeasureSpec.EXACTLY)
        )
    }
}





































//