package tv.ridal.Ui.ActionBar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.Ui.Animators.StateValueAnimator
import tv.ridal.Ui.Drawables.MultiDrawable
import tv.ridal.Ui.InstantPressListener
import tv.ridal.Ui.Layout.LayoutHelper
import tv.ridal.Utils.Utils
import kotlin.math.max

class ActionBar(context: Context) : FrameLayout(context)
{
    companion object {
        const val actionBarHeightDp: Int = 50
    }

    val actionBarHeight: Int = Utils.dp(actionBarHeightDp)

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

            this.addView(menu!!, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            ))
        }

    // animators
    private var titleAnimator: StateValueAnimator = StateValueAnimator().apply {
        duration = 170
    }


    private var actionButtonView: ImageView? = null
    private var titleFrame: FrameLayout? = null
    private var titleView: TextView? = null
    private var subtitleView: TextView? = null

    private fun createActionButtonView()
    {
        actionButtonView = ImageView(context).apply {
            isClickable = true
            scaleType = ImageView.ScaleType.CENTER

//            background = Theme.createCircleSelector(Theme.color_bg)

            setOnTouchListener(InstantPressListener(this))
        }

        this.addView(actionButtonView)
    }

    private fun createTitleView()
    {
        titleFrame = FrameLayout(context).apply {
            layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
            )
        }

        titleView = TextView(context).apply {
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
        subtitleView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text2))
            textSize = 16F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }

        titleView!!.layoutParams = LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0, 7, 0, 0

        )

        titleFrame!!.addView(subtitleView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.TOP or Gravity.CENTER_HORIZONTAL,
            0, 7 + 22 + 2, 0, 0
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



    fun onActionButtonClick(l: () -> Unit)
    {
        actionButtonView?.setOnClickListener {
            l.invoke()
        }
    }



    fun hideTitle(animated: Boolean = true)
    {
        if (titleView!!.visibility == View.GONE) return

        if (animated)
        {
            if (titleAnimator.isRunning && titleAnimator.currentState == "hiding") return

            titleAnimator.apply {
                cancel()
                removeAllListeners()

                setFloatValues(titleView!!.alpha, 0F)

                addUpdateListener {
                    titleView!!.alpha = it.animatedValue as Float
                }

                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)

                        titleView!!.visibility = View.GONE
                    }
                })

                currentState = "hiding"
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
            if (titleAnimator.isRunning && titleAnimator.currentState == "showing") return

            titleAnimator.apply {
                cancel()
                removeAllListeners()

                setFloatValues(titleView!!.alpha, 1F)

                addUpdateListener {
                    titleView!!.alpha = it.animatedValue as Float
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)

                        titleView!!.visibility = View.VISIBLE
                    }
                })

                currentState = "showing"
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


    init
    {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        var actionButtonViewWidth = 0
        if (actionButtonView != null)
        {
            actionButtonView!!.layoutParams = LayoutHelper.createFrame(
                actionBarHeightDp, actionBarHeightDp,
                Gravity.START
            )
            actionButtonViewWidth = actionBarHeightDp
        }

        var menuWidth = 0
        if (menu != null)
        {
            menu!!.layoutParams = LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            )
            menu!!.measure(0, 0)

            menuWidth = Utils.px(menu!!.measuredWidth)
        }

        if (titleFrame != null)
        {
            titleFrame!!.layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.START,
                max(actionButtonViewWidth, menuWidth), 0, max(actionButtonViewWidth, menuWidth), 0
            )
        }

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paddingTop + actionBarHeight + paddingBottom, MeasureSpec.EXACTLY))
    }

    class Menu(context: Context) : LinearLayout(context)
    {

        init
        {
            orientation = LinearLayout.HORIZONTAL
            setPadding(Utils.dp(15), 0, Utils.dp(6), 0)
        }

        fun addItem(drawable: Drawable, onClick: (() -> Unit)? = null)
        {
            val itemView = createItemView().apply {
                setImageDrawable(drawable)

                if (onClick != null) {
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
                isClickable = true
                setOnTouchListener(InstantPressListener(this))

                background = Theme.createCircleSelector(Theme.color_bigActionBar_item_bg)

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