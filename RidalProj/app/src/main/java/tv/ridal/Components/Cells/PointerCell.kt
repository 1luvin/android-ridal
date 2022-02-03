package tv.ridal.Components.Cells

import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.ApplicationActivity
import tv.ridal.Components.InstantPressListener
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

class PointerCell : FrameLayout(ApplicationActivity.instance())
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
            20, 0, 15 + 24 + 20, 0
        ))

        val pointerDrawable = Theme.drawable(R.drawable.pointer_forward_mini, Theme.color_main)
        pointerView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(pointerDrawable)
        }
        addView(pointerView, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 20, 0))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(50), MeasureSpec.EXACTLY)
        )
    }

}





































//