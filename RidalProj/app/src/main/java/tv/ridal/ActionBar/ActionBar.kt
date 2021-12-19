package tv.ridal.ActionBar

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.Components.InstantPressListener
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Utils.Utils

class ActionBar(context: Context) : FrameLayout(context)
{
    val actionBarHeightDp: Int = 56
    val actionBarHeight: Int = Utils.dp(56)

    // action button
    private var actionButtonView: ImageView? = null
    private fun createActionButtonView()
    {
        actionButtonView = ImageView(context).apply {
            isClickable = true
            scaleType = ImageView.ScaleType.CENTER

            background = Theme.createCircleSelector(Theme.color_bg)

            setOnTouchListener(InstantPressListener())
        }

        this.addView(actionButtonView)
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

    fun onActionButtonClick(l: () -> Unit)
    {
        actionButtonView?.setOnClickListener {
            l.invoke()
        }
    }


    // title
    private var titleFrame: FrameLayout? = null

    private var titleView: TextView? = null
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
    var titleTextSize: Float = 22F
        set(value) {
            field = value

            titleView?.textSize = titleTextSize
        }

    private var subtitleView: TextView? = null
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

    var subtitle: String = ""
        set(value) {
            field = value
            if (subtitleView == null) createSubtitleView()
            subtitleView!!.text = subtitle
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
                Gravity.START or Gravity.TOP
            )
            actionButtonViewWidth = actionBarHeightDp
        }

        if (titleFrame != null)
        {
            titleFrame!!.layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                Gravity.START or Gravity.TOP,
                actionButtonViewWidth, 0, actionButtonViewWidth, 0
            )
        }

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paddingTop + actionBarHeight + paddingBottom, MeasureSpec.EXACTLY))
    }

}





































//