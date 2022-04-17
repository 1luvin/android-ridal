package tv.ridal.ui.cell

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import tv.ridal.util.Theme
import tv.ridal.ui.layout.Layout
import tv.ridal.util.Utils
import tv.ridal.ui.view.RTextView

class SearchResultCell(context: Context) : FrameLayout(context)
{
    private var movieNameView: RTextView
    private var movieDataView: RTextView
    private var movieRatingView: RTextView

    var movieName: String = ""
        set(value) {
            field = value
            movieNameView.text = movieName
        }

    var movieData: String = ""
        set(value) {
            field = value
            movieDataView.text = movieData
        }

    var movieRating: String = "" // от 0 до 10
        set(value) {
            field = value

            movieRatingView.text = value
            if (movieRating != "") movieRatingView.setTextColor(colorForValue(value.toFloat()))
        }

    init
    {
        movieRatingView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_main))
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(movieRatingView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.END or Gravity.CENTER_VERTICAL,
            20, 0, 20, 0
        ))

        movieNameView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(movieNameView)

        movieDataView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text2))
            textSize = 15F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(movieDataView)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        movieRatingView.measure(0, 0)

        movieNameView.layoutParams = Layout.ezFrame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            20, 7, movieRatingView.measuredWidth, 0
        )

        movieNameView.measure(0, 0)

        movieDataView.layoutParams = Layout.ezFrame(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.TOP,
            20, 7 + 17 + 4, movieRatingView.measuredWidth, 0
        )

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