package tv.ridal.ui.cell

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Utils

class ValueCell(context: Context) : FrameLayout(context)
{
    private var keyView: RTextView
    private var valueView: RTextView

    var keyText: String = ""
        set(value) {
            field = value

            keyView.text = keyText
        }
    var keyColor: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            keyView.setTextColor(keyColor)
        }

    var valueText: String = ""
        set(value) {
            field = value

            valueView.text = valueText
        }

    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        keyView = RTextView(context).apply {
            setTextColor(Theme.color_text)
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(keyView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL))

        valueView = RTextView(context).apply {
            setTextColor(Theme.color_main)
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(valueView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.END or Gravity.CENTER_VERTICAL))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
        )

        val availableWidth = measuredWidth - paddingLeft - paddingRight
        var width = availableWidth / 2

        valueView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            0
        )

        width = availableWidth - valueView.measuredWidth - Utils.dp(10)

        keyView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            0
        )
    }

}





































//