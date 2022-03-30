package tv.ridal.UI.ActionBar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import com.github.ybq.android.spinkit.style.Pulse
import tv.ridal.Application.Theme
import tv.ridal.UI.Drawables.MultiDrawable
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.UI.View.RTextView
import tv.ridal.Application.Utils
import kotlin.math.max

class ActionBar(context: Context) : FrameLayout(context)
{
    companion object
    {
        const val actionBarHeightDp: Int = 50
    }

    val actionBarHeight: Int = Utils.dp(actionBarHeightDp)

    var divider: View? = null
        private set

    fun setDivider(color: Int, height: Int = Utils.dp(1), hide: Boolean = false)
    {
        if (divider == null)
        {
            divider = View(context).apply {
                background = Theme.rect(color)
            }
            addView(divider, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, height,
                Gravity.BOTTOM
            ))
        }
        else
        {
            divider.apply {
                updateLayoutParams<FrameLayout.LayoutParams> {
                    this.height = height
                }
                background = Theme.rect(color)
            }
        }

        if (hide) divider!!.visibility = View.GONE
    }

    var actionButtonIcon: Drawable? = null
        set(value) {
            field = value

            if (actionButtonView == null) createActionButtonView()

            actionButtonView!!.setImageDrawable(actionButtonIcon)
        }
    var actionButtonColor: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            actionButtonView?.drawable?.setTint(actionButtonColor)
        }

    var title: String = ""
        set(value) {
            field = value
            if (titleView == null) createTitleView()
            titleView!!.text = title
        }
    var titleColor: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            titleView?.setTextColor(titleColor)
        }
    var titleTypeface: Typeface = Theme.typeface(Theme.tf_bold)
        set(value) {
            field = value

            titleView?.typeface = titleTypeface
        }
    var titleTextSize: Float = 20F
        set(value) {
            field = value

            titleView?.textSize = titleTextSize
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

            addView(menu!!, LayoutHelper.frame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            ))
        }

    private var actionButtonView: ImageView? = null
    private var titleFrame: FrameLayout? = null
    private var titleView: RTextView? = null
    private var subtitleView: RTextView? = null

    private fun createActionButtonView()
    {
        actionButtonView = ImageView(context).apply {
            isClickable = true
            setOnTouchListener(InstantPressListener(this))

            scaleType = ImageView.ScaleType.CENTER

            setOnClickListener {
                actionButtonListener?.invoke()
            }
        }

        addView(actionButtonView)
    }

    private var actionButtonListener: (() -> Unit)? = null
    fun onActionButtonClick(l: () -> Unit)
    {
        actionButtonListener = l
    }

    private fun createTitleView()
    {
        titleFrame = FrameLayout(context).apply {
            layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )
        }

        titleView = RTextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            setLines(1)
            maxLines = 1
            isSingleLine = true

            ellipsize = TextUtils.TruncateAt.END

            typeface = titleTypeface
            textSize = titleTextSize
        }

        titleFrame!!.addView(titleView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.CENTER
        ))

        this.addView(titleFrame)
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

        titleView!!.apply {
            textSize = 19F
        }
        titleView!!.layoutParams = LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0, 4, 0, 0

        )

        titleFrame!!.addView(subtitleView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0, 4 + titleTextSize.toInt() + 2, 0, 0
        ))
    }


    fun setBackgrounds(layers: Array<out Drawable>)
    {
        background = MultiDrawable(layers).apply {
            crossfadeDuration = 170
        }
    }

    fun showBackground(index: Int)
    {
        if (background !is MultiDrawable) return

        (background as MultiDrawable).apply {
            show(index)
        }
    }



    fun hideTitle(animated: Boolean = true)
    {
        if (titleView!!.visibility == View.GONE) return

        if (animated)
        {
            ValueAnimator.ofFloat(titleView!!.alpha, 0F).apply {
                addUpdateListener {
                    titleView!!.alpha = it.animatedValue as Float
                }

                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        titleView!!.visibility = View.GONE
                    }
                })

                start()
            }
        }
        else
        {
            titleView!!.apply {
                alpha = 0F
                visibility = View.GONE
            }
        }
    }

    fun showTitle(animated: Boolean = true)
    {
        if (titleView!!.visibility == View.VISIBLE) return

        if (animated)
        {
            ValueAnimator.ofFloat(titleView!!.alpha, 1F).apply {
                addUpdateListener {
                    titleView!!.alpha = it.animatedValue as Float
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        titleView!!.visibility = View.VISIBLE
                    }
                })

                start()
            }
        }
        else
        {
            titleView!!.apply {
                alpha = 1F
                visibility = View.VISIBLE
            }
        }
    }

    fun hideDivider()
    {
        if (divider == null) return
        if (divider!!.visibility == View.GONE) return

        ValueAnimator.ofFloat(divider!!.alpha, 0F).apply {
            duration = 170

            addUpdateListener {
                divider!!.alpha = it.animatedValue as Float
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    divider!!.visibility = View.GONE
                }
            })

            start()
        }
    }

    fun showDivider()
    {
        if (divider == null) return
        if (divider!!.visibility == View.VISIBLE) return

        ValueAnimator.ofFloat(divider!!.alpha, 1F).apply {
            duration = 170

            addUpdateListener {
                divider!!.alpha = it.animatedValue as Float
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)

                    divider!!.visibility = View.VISIBLE
                }
            })

            start()
        }
    }

    init
    {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        var width1 = 0
        actionButtonView?.apply {
            layoutParams = LayoutHelper.frame(
                actionBarHeight, actionBarHeight,
                Gravity.START
            )
            width1 = actionBarHeight
        }

        var width2 = 0
        menu?.apply {
            measure(0, 0)
            width2 = measuredWidth
        }

        val busyWidth = max(width1, width2)
        titleFrame?.apply {
            layoutParams = LayoutHelper.frame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.START,
                busyWidth, 0, busyWidth, 0
            )
        }

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paddingTop + actionBarHeight + paddingBottom, MeasureSpec.EXACTLY))
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

            this.addView(itemView, LayoutHelper.createLinear(
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

            addView(loadingView, LayoutHelper.createLinear(
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