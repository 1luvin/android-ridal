package tv.ridal.UI.Cells

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

class PointerCell(context: Context) : FrameLayout(context)
{
    private var textView: TextView
    private var pointerView: ImageView

    var text: String = ""
        set(value) {
            field = value

            textView.text = text
        }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener(this))

        setPadding(Utils.dp(20), 0, Utils.dp(20), 0)

        textView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(textView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            0, 0, 15 + 24, 0
        ))

        val pointerDrawable = Theme.drawable(R.drawable.pointer_forward, Theme.color_main)
        pointerView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(pointerDrawable)
        }
        addView(pointerView, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(40), MeasureSpec.EXACTLY)
        )
    }

}





































//