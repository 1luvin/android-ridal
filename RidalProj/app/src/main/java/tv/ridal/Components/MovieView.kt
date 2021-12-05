package tv.ridal.Components

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import coil.imageLoader
import coil.request.ImageRequest
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.HDRezka.Loader
import tv.ridal.Utils.Utils
import kotlin.math.ceil

class MovieView(context: Context) : FrameLayout(context)
{
    private var posterView: ImageView
    var posterUrl: String = ""
        set(value) {
            field = value

            Loader.loadImage(posterUrl, this::onPosterLoaded)
        }

    private fun onPosterLoaded(poster: Drawable?)
    {
        // ошибка загрузки постера
        if (poster == null) return

        val rawBitmap = (poster as BitmapDrawable).bitmap

        val resizedBitmap = Bitmap.createScaledBitmap(rawBitmap, posterWidthPx, posterHeightPx, false)

        posterDrawable = BitmapDrawable(resources, resizedBitmap)
        posterView.setImageDrawable(posterDrawable)
    }

    private var posterDrawable: BitmapDrawable? = null
    var posterWidthPx = Utils.dp(113)
        set(value) {
            field = value

            posterHeightPx = ceil(posterWidthPx * 1.5F).toInt()

            posterView.layoutParams = LayoutHelper.createFrame(
                Utils.px(posterWidthPx), Utils.px(posterHeightPx),
                Gravity.START or Gravity.TOP
            )

            gradient = LinearGradient(
                0F, posterHeightPx + 0F, 0F, posterHeightPx / 2F,
                Theme.color(Theme.color_bg),
                Theme.COLOR_TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            gradientPaint.shader = gradient
        }

    private var posterHeightPx = Utils.dp(170)

    private var movieTypeView: TextView
    var movieType: String = ""
        set(value) {
            field = value

            movieTypeView.text = movieType
        }

    private var gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var gradient: LinearGradient

    private var movieNameView: TextView
    var movieName: String? = null
        set(value) {
            field = value

            movieNameView.text = movieName
        }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener())

        background = Theme.createRect(Theme.color(Theme.color_bg))
        foreground = Theme.createRectSelector(Theme.color(Theme.color_main))

        posterView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_XY
        }
        addView(posterView, LayoutHelper.createFrame(
            Utils.px(posterWidthPx), Utils.px(posterHeightPx),
            Gravity.START or Gravity.TOP
        ))

        movieTypeView = TextView(context).apply {
            setPadding(Utils.dp(5), Utils.dp(2), Utils.dp(5), Utils.dp(2))

            background = Theme.createRect(Theme.alphaColor(Theme.COLOR_LIGHT_CHERRY, 0.7F), floatArrayOf(
                0F, 0F, 0F, Utils.dp(7F)
            ))

            setTextColor(Theme.COLOR_WHITE)
            textSize = 13.3F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
        }
        addView(movieTypeView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.END or Gravity.TOP
        ))


        gradient = LinearGradient(
            0F, posterHeightPx + 0F, 0F, posterHeightPx / 2F,
            Theme.color(Theme.color_bg),
            Theme.COLOR_TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        gradientPaint.shader = gradient

        movieNameView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 15F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(movieNameView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.BOTTOM,
            7, 0, 7, 5
        ))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        if (widthMode == MeasureSpec.UNSPECIFIED)
        {
            posterWidthPx = Utils.dp(113)
        }
        else if (widthMode == MeasureSpec.EXACTLY)
        {
            posterWidthPx = width
        }


        super.onMeasure(
            MeasureSpec.makeMeasureSpec(posterWidthPx, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(Utils.dp(Utils.px(posterHeightPx) + 5 + 15 + 5), MeasureSpec.EXACTLY)
        )
    }

    override fun dispatchDraw(canvas: Canvas)
    {
        super.dispatchDraw(canvas)

        canvas.drawRect(0F, posterHeightPx / 2F, posterWidthPx + 0F, posterHeightPx + Utils.dp(1F), gradientPaint)
    }

}





































//