package tv.ridal.ui.actionbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import tv.ridal.utils.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.utils.Utils

class BigActionBar(context: Context) : FrameLayout(context)
{
    private var titleView: TextView

    var title: String = ""
        set(value) {
            field = value

            titleView.text = title

            titleView.measure(0, 0)
        }
    var titleColor: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            titleView.setTextColor(titleColor)
        }

    var menu: BigActionBar.Menu? = null
        set(value) {
            value ?: return
            field = value

//            menu!!.measure(0, 0)
//            val menuWidth = menu!!.measuredWidth

            this.addView(menu!!, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            ))
        }

    init
    {
        titleView = TextView(context).apply {
            setTextColor(titleColor)
            textSize = 36F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(titleView, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            25, 0, 25, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        if (menu != null)
        {
            menu!!.measure(0, 0)
            val menuWidth = menu!!.measuredWidth

            titleView.layoutParams = Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                25, 0, Utils.px(menuWidth), 0
            )
        }

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            paddingTop + Utils.dp(90) + paddingBottom
        )
    }

    class Menu(context: Context) : LinearLayout(context)
    {
        init
        {
            setPadding(Utils.dp(15), 0, Utils.dp(25), 0)
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

            addView(itemView, Layout.ezLinear(
                40, 40,
                if (itemsCount() != 0) 15 else 0, 0, 0, 0
            ))
        }

        private fun createItemView() : ImageView
        {
            return ImageView(context).apply {
                isClickable = true
                setOnTouchListener(InstantPressListener(this))

                background = Theme.rect( Theme.overlayColor(Theme.color_bg), radii = FloatArray(4).apply {
                    fill( Utils.dp(10F) )
                } )

                scaleType = ImageView.ScaleType.CENTER
            }
        }

        private fun itemsCount(): Int = this.childCount

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            var width = paddingLeft + Utils.dp(40) * itemsCount()
            if (itemsCount() > 1) {
                width += Utils.dp(15) * (itemsCount() - 1)
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