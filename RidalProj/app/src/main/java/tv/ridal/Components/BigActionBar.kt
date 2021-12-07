package tv.ridal.Components

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.View.LoadingTextView
import tv.ridal.Utils.Utils

class BigActionBar(context: Context) : FrameLayout(context)
{
    var titleView: TextView? = null
        set(value) {
            value ?: return
            field = value

            if (titleView!!.parent != null) return

            addView(titleView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                30, 0, 15 + 40 + 25, 0
            ))
        }

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