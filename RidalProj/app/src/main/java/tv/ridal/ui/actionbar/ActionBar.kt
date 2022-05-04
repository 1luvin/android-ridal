package tv.ridal.ui.actionbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.updateLayoutParams
import tv.ridal.R
import tv.ridal.util.Theme
import tv.ridal.ui.drawable.MultiDrawable
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.measure
import tv.ridal.ui.asInt
import tv.ridal.ui.setTextColor
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Utils
import kotlin.math.max

class ActionBar(context: Context) : FrameLayout(context)
{
    companion object
    {
        const val actionBarHeightDp: Int = 50
    }

    val actionBarHeight: Int = Utils.dp( actionBarHeightDp )


    private lateinit var titleView: RTextView

    private var subtitleView: RTextView? = null

    private var backButton: ImageView? = null
    private var onBackListener: (() -> Unit)? = null
    fun onBack(l: (() -> Unit)?)
    {
        if ( l == null )
        {
            backButton?.let {
                removeView(it)
            }
            backButton = null
        }
        else
        {
            if ( backButton == null ) createBackButtonView()
        }

        onBackListener = l
    }

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

    var title: String = ""
        set(value) {
            field = value

            titleView.text = title
        }
    private val titleTypeface: Typeface = Theme.typeface(Theme.tf_bold)
    private val titleTextSize: Float = 19F

    var subtitle: String = ""
        set(value) {
            field = value

            if ( subtitle == "" )
            {
                subtitleView?.let {
                    removeView(it)
                }
                subtitleView = null
            }
            else
            {
                if (subtitleView == null) createSubtitleView()
                subtitleView!!.text = subtitle
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
        backButton?.let {
            it.measure(
                MeasureSpec.makeMeasureSpec( actionBarHeight, MeasureSpec.EXACTLY ),
                MeasureSpec.makeMeasureSpec( actionBarHeight, MeasureSpec.EXACTLY )
            )
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)

        var leftMargin = 0
        backButton?.let {
            leftMargin = it.measuredWidth
        }

        var topMargin = 0
        subtitleView?.let {
            topMargin = Utils.dp(3)
        }

        var rightMargin = 0
        menu?.let {
            rightMargin = it.measuredWidth
        }

        var gravity = Gravity.CENTER
        subtitleView?.let {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }

        val widthMargin = max(leftMargin, rightMargin)

        titleView.apply {
            updateLayoutParams<FrameLayout.LayoutParams> {
                this.gravity = gravity
                setMargins( widthMargin, topMargin, widthMargin, this.bottomMargin )
            }
        }

        subtitleView?.apply {
            topMargin += ( Utils.dp( titleView.textSize.toInt() ) + Utils.dp(3) )

            updateLayoutParams<FrameLayout.LayoutParams> {
                setMargins( widthMargin, topMargin, widthMargin, this.bottomMargin  )
            }
        }
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
            setTextColor( Theme.color_text2 )
            textSize = 15F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }

        addView(subtitleView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL
        ))
    }

    private fun createBackButtonView()
    {
        backButton = ImageView(context).apply {
            isClickable = true
            setOnTouchListener( InstantPressListener(this) )

            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable( Theme.drawable( R.drawable.back, Theme.mainColor ) )

            setOnClickListener {
                onBackListener?.invoke()
            }
        }

        addView(backButton, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START
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
                    val value = it.animatedValue as Float

                    elevation = Utils.dp(5F) * value
                    titleView.alpha = value
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
            elevation = if (enable) 0F else Utils.dp(5F)
            titleView.apply {
                alpha = toAlpha
                visibility = if (enable) View.GONE else View.VISIBLE
            }
        }
    }


    open class Menu(context: Context) : LinearLayout(context)
    {
        init
        {
            setPadding( Utils.dp(15), 0, Utils.dp(6), 0 )
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

        private fun createItemView() : ImageView
        {
            return ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
            }
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

        private fun itemsCount(): Int
        {
            return this.childCount
        }
    }

}





































//