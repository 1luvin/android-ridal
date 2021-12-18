package tv.ridal.Cells

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

class FilterCell : FrameLayout(ApplicationActivity.instance())
{
    private var nameView: TextView
    private var valueView: TextView
    private var pointerView: ImageView

    var filterName: String = ""
    set(value) {
        field = value

        nameView.text = filterName
    }

    var filterValue: String = ""
        set(value) {
            field = value

            valueView.text = filterValue
        }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener())

        background = Theme.createOutlinedRectSelector(
            Theme.color(Theme.color_bg),
            Theme.Outline(Theme.ripplizeColor(Theme.color_bg)),
            FloatArray(4).apply {
                fill(Utils.dp(10F))
            }
        )

        nameView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_main))
            textSize = 16F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(nameView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            15, 10, 15 + 24 + 15, 0)
        )

        valueView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(valueView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            15, 10 + 16 + 5, 15 + 24 + 15, 10))

        val pointerDrawable = Theme.drawable(R.drawable.pointer_forward, Theme.color_main)
        pointerView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(pointerDrawable)
        }
        addView(pointerView, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 15, 0))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED)
        )
    }
}





































//