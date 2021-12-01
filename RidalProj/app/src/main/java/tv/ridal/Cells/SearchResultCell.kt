package tv.ridal.Cells

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Utils.Utils

class SearchResultCell(context: Context) : FrameLayout(context)
{
    private var textView: TextView
    private var textView2: TextView

    var resultText: String = ""
        set(value) {
            field = value
            textView.text = resultText
        }

    var resultValue: Float = 0F // от 0 до 10
        set(value) {
            field = value

            textView2.text = value.toString()
            textView2.setTextColor(colorForValue(value))
        }

    init
    {
        textView2 = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_main))
            textSize = 18F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView2, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.END or Gravity.CENTER_VERTICAL,
            20, 0, 25, 0
        ))

        textView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 18F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            25, 0, 20 + textView2.width + 25, 0)
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(50), MeasureSpec.EXACTLY)
        )
    }

    // value: от 0 до 10
    private fun colorForValue(value: Float) : Int
    {
        if (value >= 5F)
        {
            return Theme.mixColors(
                Theme.color(Theme.color_searchResult_middle),
                Theme.color(Theme.color_searchResult_best),
                (value - 5F) / 5F
            )
        }
        return Theme.mixColors(
            Theme.color(Theme.color_searchResult_worst),
            Theme.color(Theme.color_searchResult_middle),
            (value / 5F)
        )
    }

}


































//