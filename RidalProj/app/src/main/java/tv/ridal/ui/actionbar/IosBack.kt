package tv.ridal.ui.actionbar

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import tv.ridal.R
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.msg
import tv.ridal.ui.setPaddings
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class IosBack(context: Context) : LinearLayout(context)
{
    var type: IosBack.Type = IosBack.Type.ICON_TEXT
        set(value)
        {
            if (field == value) return
            field = value

            if ( ! canChangeType ) return

            when (type)
            {
                Type.ICON_TEXT -> applyIconText()
                Type.ICON -> applyIcon()
            }
        }

    var canChangeType: Boolean = true
    var backText: String? = null


    private var onBack: (() -> Unit)? = null
    fun onBack(l: (() -> Unit)?)
    {
        onBack = l
    }


    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        setOnClickListener {
            onBack?.invoke()
        }

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
            10, Layout.WRAP_CONTENT,
            Gravity.CENTER_VERTICAL
        ))

        val textView = RTextView(context).apply {
            setTextColor( Theme.mainColor )
            textSize = 17.5F
            typeface = Theme.typeface(Theme.tf_normal)

            this.text = backText ?: Locale.string(R.string.back)
        }
        addView(textView, Layout.ezLinear(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.CENTER_VERTICAL
        ))
    }

    private fun applyIcon()
    {
        removeAllViews()

        setPaddings(0)

        val imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable( Theme.drawable(R.drawable.back, Theme.mainColor) )
        }
        addView(imageView, Layout.ezLinear(
            ActionBar.actionBarHeightDp, ActionBar.actionBarHeightDp
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            0,
            MeasureSpec.makeMeasureSpec( Utils.dp(ActionBar.actionBarHeightDp), MeasureSpec.EXACTLY )
        )
    }


    enum class Type
    {
        ICON_TEXT, ICON
    }
}

































//