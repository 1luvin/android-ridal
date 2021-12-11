package tv.ridal.Components.ActionBar

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Utils.Utils

class BigActionBar(context: Context) : FrameLayout(context)
{
    private var titleView: TextView

    var title: String = ""
        set(value) {
            field = value

            titleView?.text = title

            titleView?.measure(0, 0)
        }

    private var imageView: ImageView

    var image: Drawable? = null
        set(value) {
            field = value

            imageView.setImageDrawable(image)
        }

    var imageClickListener: View.OnClickListener? = null
        set(value) {
            field = value

            imageView.setOnClickListener(imageClickListener)
        }

    init
    {
        setPadding(0, Utils.dp(25), 0, 0)

        titleView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 36F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(titleView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            30, 0, 15 + 40 + 25, 0
        ))

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_XY
        }
        addView(imageView, LayoutHelper.createFrame(
            40, 40,
            Gravity.END or Gravity.CENTER_VERTICAL,
            0, 0, 25, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paddingTop + Utils.dp(90) + paddingBottom, MeasureSpec.EXACTLY)
        )
    }
}





































//