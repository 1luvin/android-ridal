package tv.ridal.ui.actionbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.children
import androidx.core.view.contains
import androidx.core.view.updateLayoutParams
import com.github.ybq.android.spinkit.style.Pulse
import tv.ridal.R
import tv.ridal.util.Theme
import tv.ridal.ui.drawable.MultiDrawable
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.measure
import tv.ridal.ui.msg
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Locale
import tv.ridal.util.Utils

class ActionBar(context: Context) : FrameLayout(context)
{
    companion object
    {
        const val actionBarHeightDp: Int = 50
    }

    private var iosBack: IosBack? = null
    private lateinit var titleView: RTextView
    private var subtitleView: RTextView? = null


    fun addIosBack(text: String? = null, type: IosBack.Type = IosBack.Type.ICON_TEXT)
    {
        iosBack?.let {
            if ( contains(it) ) removeView(it)
        }

        iosBack = IosBack(context, text).apply {
            this.type = type

            setOnClickListener {
                onBack?.invoke()
            }
        }

        addView(iosBack, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
            Gravity.START
        ))
    }

    private var onBack: (() -> Unit)? = null
    fun onBack(l: () -> Unit)
    {
        onBack = l
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
        }

    var menu: ActionBar.Menu? = null
        set(value) {
            value ?: return
            field = value

            // если меню уже имеется
            if ( children.contains(menu as View) )
            {
                removeView(menu)
            }

            addView(menu!!, Layout.frame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            ))
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


    fun enableOnlyBackButton(enable: Boolean, animated: Boolean = true)
    {
        if ( background !is MultiDrawable )
        {
            background = MultiDrawable(
                arrayOf(
                    background,
                    Theme.rect(
                        Theme.Fill( intArrayOf(Theme.alphaColor(Theme.COLOR_BLACK, 0.5F), Theme.COLOR_TRANSPARENT), GradientDrawable.Orientation.TOP_BOTTOM )
                    )
                ),
                show = if (enable) 1 else 0
            )
        }

        // background
        (background as MultiDrawable).apply {
            if (animated) crossfadeDuration = 170
            show( if (enable) 1 else 0, animated )
        }
        // title
        val toAlpha = if (enable) 0F else 1F
        if (animated)
        {
            ValueAnimator.ofFloat(titleView.alpha, toAlpha).apply {
                duration = 170

                addUpdateListener {
                    titleView.alpha = it.animatedValue as Float
                }

                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationStart(animation: Animator?)
                    {
                        super.onAnimationStart(animation)

                        if ( ! enable) titleView.visibility = View.VISIBLE
                    }
                    override fun onAnimationEnd(animation: Animator?) {
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
            MeasureSpec.makeMeasureSpec( paddingTop + actionBarHeight + paddingBottom, MeasureSpec.EXACTLY) )

        var leftWidth = 0
        iosBack?.let {
//            msg("${it.childCount}")
//            it.measure()
            leftWidth = it.measuredWidth
        }

        var rightWidth = 0
        menu?.let {
            it.measure()
            rightWidth = it.measuredWidth
        }

        var availableWidth = measuredWidth - leftWidth - rightWidth

        val paint = Paint().apply {
            typeface = Theme.typeface(Theme.tf_normal)
            textSize = titleView.textSize
        }
        val textWidth = paint.measureText(title)

        if ( textWidth > availableWidth )
        {
            msg("\n\nXXD\n\n")
//            iosBack?.apply {
//                type = IosBack.Type.ICON
//            }
            // исправиль ошибку
        }

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

        subtitleView?.let {
            titleView.apply {
                updateLayoutParams<FrameLayout.LayoutParams> {
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    setMargins( 0, Utils.dp(4), 0, 0 )
                }
            }
        }
    }


    class IosBack(context: Context, private val Text: String? = null) : LinearLayout(context)
    {
        var type: IosBack.Type = IosBack.Type.ICON_TEXT
            set(value)
            {
                if (field == value) return
                field = value

                when (type)
                {
                    Type.ICON -> applyIcon()
                    Type.ICON_TEXT -> applyIconText()
                }
            }

        init
        {
            isClickable = true
            setOnTouchListener( InstantPressListener(this) )

            applyIconText()
        }

        private fun applyIconText()
        {
            removeAllViews()

            setPadding( 0, 0, Utils.dp(10), 0 )

            val backIcon = Theme.drawable(R.drawable.ios_back, Theme.mainColor)
            val imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER

                setImageDrawable(backIcon)
            }
            addView(imageView, Layout.ezLinear(
                Utils.dp(10), Layout.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            ))

            val textView = RTextView(context).apply {
                setTextColor( Theme.mainColor )
                textSize = 17.5F
                typeface = Theme.typeface(Theme.tf_normal)

                this.text = Text ?: Locale.string(R.string.back)
            }
            addView(textView, Layout.ezLinear(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            ))
        }

        private fun applyIcon()
        {
            removeAllViews()

            setPadding( 0, 0, 0, 0 )

            val imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER

                setImageDrawable( Theme.drawable(R.drawable.back, Theme.mainColor) )
            }
            addView(imageView, Layout.ezLinear(
                actionBarHeightDp, actionBarHeightDp
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(
                0,
                MeasureSpec.makeMeasureSpec( Utils.dp(actionBarHeightDp), MeasureSpec.EXACTLY )
            )
        }

        enum class Type
        {
            ICON_TEXT, ICON
        }
    }

    open class Menu(context: Context) : LinearLayout(context)
    {
        init
        {
            setPadding(Utils.dp(15), 0, Utils.dp(6), 0)
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

    class LoadingMenu(context: Context) : Menu(context)
    {
        private var loadingView: ProgressBar

        init
        {
            setPadding(Utils.dp(15), 0, 0, 0)

            loadingView = ProgressBar(context).apply {
                indeterminateDrawable = Pulse().apply {
                    color = Theme.color(Theme.color_main)
                }

                visibility = View.GONE // загрузка скрыта по умолчанию
            }

            addView(loadingView, Layout.ezLinear(
                24, 24
            ))
        }

        fun showLoading()
        {
            loadingView.apply {
                visibility = View.VISIBLE
            }
        }

        fun stopLoading()
        {
            loadingView.apply {
                visibility = View.GONE
            }
        }
    }

    fun showLoading()
    {
        if (menu == null || menu !is LoadingMenu) return

        (menu as LoadingMenu).showLoading()
    }

    fun hideLoading()
    {
        if (menu == null || menu !is LoadingMenu) return

        (menu as LoadingMenu).stopLoading()
    }

}





































//