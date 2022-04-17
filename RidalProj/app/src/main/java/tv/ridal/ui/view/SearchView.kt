package tv.ridal.ui.view

import android.content.Context
import android.text.*
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.ui.layout.Layout
import tv.ridal.R
import tv.ridal.util.Utils

class SearchView(context: Context) : FrameLayout(context)
{
    private var searchIconView: ImageView
    private var textView: TextView

    var text: String = ""
        set(value) {
            field = value

            textView.text = text
        }

    init
    {
        searchIconView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(Theme.drawable(R.drawable.search, Theme.color_text))
        }
        addView(searchIconView, Layout.ezFrame(
            50, 50
        ))

        textView = RTextView(context).apply {
            gravity = Gravity.CENTER_VERTICAL

            setTextColor(Theme.color(Theme.color_text))
            textSize = 18F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            hint = Locale.text(Locale.hint_search)
        }
        addView(textView, Layout.ezFrame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            50, 0, 0, 0
        ))
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