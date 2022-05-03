package tv.ridal.ui.actionbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.contains
import androidx.core.view.updateLayoutParams
import tv.ridal.R
import tv.ridal.util.Theme
import tv.ridal.ui.drawable.MultiDrawable
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.measure
import tv.ridal.ui.asInt
import tv.ridal.ui.msg
import tv.ridal.ui.setPaddings
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Locale
import tv.ridal.util.Utils
import kotlin.math.max

class ActionBar(context: Context) : FrameLayout(context)
{
    companion object
    {
        const val actionBarHeightDp: Int = 50
    }


    var iosBack: IosBack? = null
        set(value)
        {
            iosBack?.let {
                if ( contains(it) ) removeView(it)
            }

            field = value

            iosBack?.let {
                addView(it, Layout.ezFrame(
                    Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
                    Gravity.START
                ))
            }
        }

    lateinit var titleView: RTextView
        private set

    private var subtitleView: RTextView? = null
    var menu: ActionBar.Menu? = null
        set(value) {
            menu?.let {
                removeView(it)
            }

            field = value
            if (menu == null) return

            addView(menu!!, Layout.frame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            ))
        }


    val actionBarHeight: Int = Utils.dp(actionBarHeightDp)

    var title: String = ""
        set(value) {
            field = value

            titleView.text = title
        }
    var titleColor: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            titleView.setTextColor(titleColor)
        }
    var titleTypeface: Typeface = Theme.typeface(Theme.tf_bold)
        set(value) {
            field = value

            titleView.typeface = titleTypeface
        }
    var titleTextSize: Float = 19F
        set(value) {
            field = value

            titleView.textSize = titleTextSize
        }

    var subtitle: String = ""
        set(value) {
            field = value

            if (subtitleView == null) createSubtitleView()
            subtitleView!!.text = subtitle

            measureTitles()
        }

    private fun createTitleView()
    {
        titleView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            setLines(1)
            maxLines = 1
            isSingleLine = true

            ellipsize = TextUtils.TruncateAt.END

            typeface = titleTypeface
            textSize = titleTextSize
        }

        addView(titleView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.CENTER
        ))
    }

    private fun createSubtitleView()
    {
        subtitleView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text2))
            textSize = 15F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(subtitleView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0, 4 + titleTextSize.toInt() + 2, 0, 0
        ))
    }


    private val ANIM_DURATION: Long = 170
    fun enableOnlyBackButton(enable: Boolean, animated: Boolean = true)
    {
        if ( background !is MultiDrawable )
        {
            background = MultiDrawable(
                arrayOf(
                    background,
                    Theme.rect(
                        Theme.Fill(
                            intArrayOf( Theme.alphaColor( Color.BLACK, 0.5F ), Color.TRANSPARENT ),
                            GradientDrawable.Orientation.TOP_BOTTOM
                        )
                    )
                ),
                show = enable.asInt()
            )
        }

        // Background
        (background as MultiDrawable).apply {
            if (animated) crossfadeDuration = ANIM_DURATION
            show( enable.asInt(), animated )
        }

        // Title
        val toAlpha = if (enable) 0F else 1F
        if (animated)
        {
            ValueAnimator.ofFloat(titleView.alpha, toAlpha).apply {
                duration = ANIM_DURATION

                addUpdateListener {
                    titleView.alpha = it.animatedValue as Float
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?)
                    {
                        super.onAnimationStart(animation)

                        if ( ! enable) titleView.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animation: Animator?)
                    {
                        super.onAnimationEnd(animation)

                        if (enable) titleView.visibility = View.GONE
                    }
                })

                start()
            }
        }
        else
        {
            titleView.apply {
                alpha = toAlpha
                visibility = if (enable) View.GONE else View.VISIBLE
            }
        }
    }


    init
    {
        createTitleView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( paddingTop + actionBarHeight + paddingBottom, MeasureSpec.EXACTLY )
        )

        var leftWidth = actionBarHeight
        iosBack?.let {
            it.measure()
            leftWidth = it.measuredWidth
        }

        var rightWidth = actionBarHeight
        menu?.let {
            it.measure()
            rightWidth = it.measuredWidth
        }

        val busyWidth = max(leftWidth, rightWidth)
        val availableWidth = measuredWidth - busyWidth * 2

        titleView.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            0
        )

        subtitleView?.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            0
        )
    }

    private fun measureTitles()
    {
        measure()

        val availableWidth = max( titleView.measuredWidth, subtitleView?.measuredWidth ?: 0 )

        msg("${availableWidth}")

        if ( iosBack != null && iosBack!!.canChangeType )
        {
            val titleWidth = measureText(titleView)
            val subtitleWidth = measureText(subtitleView)
            val textWidth = max(titleWidth, subtitleWidth)

            if ( textWidth > availableWidth ) {
                iosBack!!.type = IosBack.Type.ICON
            } else {
                iosBack!!.type = IosBack.Type.ICON_TEXT
            }
        }
    }

    private val textPaint: Paint = Paint()
    private fun measureText(textView: TextView?) : Float
    {
        if ( textView == null ) return 0F

        textPaint.apply {
            typeface = textView.typeface
            textSize = Utils.dp( textView.textSize )
        }

        val text = textView.text

        return textPaint.measureText(text, 0, text.length)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)

        var leftMargin = 0
        iosBack?.let {
            leftMargin = it.measuredWidth
            println(leftMargin)
        }

        var topMargin = 0
        subtitleView?.let {
            topMargin = Utils.dp(4)
        }

        var rightMargin = 0
        menu?.let {
            rightMargin = it.measuredWidth
        }

        var widthMargin = max(leftMargin, rightMargin)

        var gravity = Gravity.CENTER
        subtitleView?.let {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }

        titleView.apply {
            updateLayoutParams<FrameLayout.LayoutParams> {
                this.gravity = gravity
                setMargins( widthMargin, topMargin, widthMargin, this.bottomMargin )
            }
        }

        subtitleView?.apply {
            updateLayoutParams<FrameLayout.LayoutParams> {
                this.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                setMargins( widthMargin, this.topMargin, widthMargin, this.bottomMargin  )
            }
        }
    }

    open class Menu(context: Context) : LinearLayout(context)
    {
        init
        {
            setPadding( Utils.dp(15), 0, Utils.dp(6), 0 )
        }

        fun addItem(drawable: Drawable, onClick: (() -> Unit)? = null)
        {
            val itemView = createItemView().apply {
                setImageDrawable(drawable)

                if (onClick != null) {
                    isClickable = true
                    setOnTouchListener(InstantPressListener(this))
                    setOnClickListener {
                        onClick.invoke()
                    }
                }
            }

            this.addView(itemView, Layout.ezLinear(
                40, 40,
                if (itemsCount() != 0) 12 else 0, 0, 0, 0
            ))
        }

        private fun createItemView() : ImageView
        {
            return ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
            }
        }

        private fun itemsCount(): Int
        {
            return this.childCount
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            var width = paddingLeft + Utils.dp(40) * itemsCount()
            if (itemsCount() > 1) {
                width += Utils.dp(12) * (itemsCount() - 1)
            }
            width += paddingRight

            super.onMeasure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED)
            )
        }
    }

}





































//