package tv.ridal.ui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.hdrezka.Loader
import tv.ridal.util.Utils
import kotlin.math.ceil

class MovieView(context: Context) : FrameLayout(context)
{
    private lateinit var posterView: ImageView
    private lateinit var detailView: TextView
    private lateinit var movieNameView: TextView


    var posterUrl: String = ""
        set(value) {
            field = value

            Loader.loadImage(posterUrl, this::onPosterLoaded)
        }
    private var posterDrawable: Drawable? = null
    private var posterWidth: Int = Utils.dp(113)
        set(value) {
            if (value == field) return
            field = value

            posterHeight = ceil(posterWidth * 1.5F).toInt()
        }
    private var posterHeight: Int = Utils.dp(170)

    var detailText: String = ""
        set(value) {
            field = value

            detailView.text = detailText
        }

    private var gradientPaint = Paint( Paint.ANTI_ALIAS_FLAG )

    var movieName: String? = null
        set(value) {
            field = value

            movieNameView.text = movieName
        }

    private fun onPosterLoaded(poster: Drawable?)
    {
        if (poster == null) return

        val rawBitmap = (poster as BitmapDrawable).bitmap

        val resizedBitmap = Bitmap.createScaledBitmap(rawBitmap, posterWidth, posterHeight, false)

        posterDrawable = RoundedBitmapDrawableFactory.create(resources, resizedBitmap).apply {
            cornerRadius = Utils.dp(6F)
        }

        posterView.setImageDrawable(posterDrawable)
    }


    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        background = Theme.rect( Theme.color_bg )

        createUI()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        if ( widthMode == MeasureSpec.UNSPECIFIED ) {
            posterWidth = Utils.dp(113)
        } else if ( widthMode == MeasureSpec.EXACTLY ) {
            posterWidth = width
        }

        val nameHeight = Utils.dp(30)

        super.onMeasure(
            MeasureSpec.makeMeasureSpec( posterWidth, MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( posterHeight + nameHeight, MeasureSpec.EXACTLY )
        )

        posterView.measure(
            MeasureSpec.makeMeasureSpec( posterWidth, MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( posterHeight, MeasureSpec.EXACTLY )
        )

        val availableWidth = measuredWidth - Utils.dp(20)
        detailView.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            0
        )

        movieNameView.measure(
            MeasureSpec.makeMeasureSpec( measuredWidth, MeasureSpec.AT_MOST ),
            MeasureSpec.makeMeasureSpec( nameHeight, MeasureSpec.AT_MOST )
        )
    }

    override fun dispatchDraw(canvas: Canvas)
    {
        super.dispatchDraw(canvas)

        val x1 = 0F
        val y1 = posterHeight + 0F
        val x2 = posterWidth + 0F
        val y2 = y1 / 2

        gradientPaint.shader = LinearGradient(
            x1, y1, x1, y2,
            Theme.color( Theme.color_bg ),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )

        canvas.drawRect( x1, y1, x2, y2 + Utils.dp(1F), gradientPaint )
    }


    private fun createUI()
    {
        createPosterView()
        addView(posterView, Layout.frame(
            posterWidth, posterHeight,
            Gravity.START or Gravity.TOP
        ))

        createDetailView()
        addView(detailView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.END or Gravity.TOP
        ))

        createNameView()
        addView(movieNameView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.BOTTOM
        ))
    }

    private fun createPosterView()
    {
        posterView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun createDetailView()
    {
        detailView = TextView(context).apply {
            setPadding( Utils.dp(5), Utils.dp(2), Utils.dp(5), Utils.dp(2) )

            background = Theme.rect(
                Theme.alphaColor(Theme.COLOR_LIGHT_CHERRY, 0.7F),
                radii = floatArrayOf(
                    0F, Utils.dp(6F), 0F, Utils.dp(7F)
                )
            )

            setTextColor( Color.WHITE )
            textSize = 13F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
        }
    }

    private fun createNameView()
    {
        movieNameView = RTextView(context).apply {
            setTextColor( Theme.color_text )
            textSize = 15.3F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
    }

}





































//