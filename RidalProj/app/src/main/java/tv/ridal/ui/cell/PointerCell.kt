package tv.ridal.ui.cell

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.R
import tv.ridal.util.Utils
import tv.ridal.ui.view.RTextView

class PointerCell(context: Context) : FrameLayout(context)
{
    private var textView: RTextView
    private var pointerView: ImageView

    var text: String = ""
        set(value) {
            field = value

            textView.text = text
        }
    var textColor: Int
        get() = textView.currentTextColor
        set(value) = textView.setTextColor(value)

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener(this))

        setPadding(Utils.dp(20), 0, Utils.dp(15), 0)

        textView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView, Layout.ezFrame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            0, 0, 24, 0))

        val pointer = Theme.drawable(R.drawable.pointer_forward, Theme.color_main)
        pointerView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(pointer)
        }
        addView(pointerView, Layout.ezFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val height: Int = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(heightMeasureSpec)
        } else Utils.dp(42)

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
    }

}





































//