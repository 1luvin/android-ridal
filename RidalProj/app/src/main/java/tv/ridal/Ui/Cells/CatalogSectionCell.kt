package tv.ridal.Ui.Cells

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import tv.ridal.Application.Theme
import tv.ridal.Ui.InstantPressListener
import tv.ridal.Ui.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

class CatalogSectionCell(context: Context) : FrameLayout(context)
{

    private var sectionNameView: TextView
    var sectionName: String = ""
        set(value) {
            field = value
            sectionNameView.text = sectionName
        }

    private var sectionSubtextView: TextView
    var sectionSubtext: String = ""
        set(value) {
            field = value
            sectionSubtextView.text = sectionSubtext
        }

    private var pointerImage: ImageView

    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        sectionNameView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 21F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(sectionNameView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            25, 7, 59, 0)
        )

        sectionSubtextView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text2))
            textSize = 14F
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(sectionSubtextView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            25, 29, 59, 0))

        val pointerDrawable = Theme.drawable(R.drawable.pointer_forward).apply {
            setTint(Theme.color(Theme.color_main))
        }
        pointerImage = ImageView(context).apply {
            setImageDrawable(pointerDrawable)
        }
        addView(pointerImage, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL,
            15, 0, 20, 0))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(56), MeasureSpec.EXACTLY)
        )
    }



}



































//