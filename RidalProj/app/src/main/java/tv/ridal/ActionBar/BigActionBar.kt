package tv.ridal.ActionBar

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.updateLayoutParams
import tv.ridal.Application.Theme
import tv.ridal.Components.InstantPressListener
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Utils.Utils

class BigActionBar(context: Context) : FrameLayout(context)
{
    private var titleView: TextView

    var title: String = ""
        set(value) {
            field = value

            titleView?.text = title

            titleView?.measure(0, 0)
        }

    var menu: BigActionBar.Menu? = null
        set(value) {
            value ?: return
            field = value

//            menu!!.measure(0, 0)
//            val menuWidth = menu!!.measuredWidth

            this.addView(menu!!, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL
            ))
        }

    init
    {
        setPadding(0, Utils.dp(25), 0, 0)

        titleView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 36F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(titleView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL,
            30, 0, 25, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        if (menu != null)
        {
            menu!!.measure(0, 0)
            val menuWidth = menu!!.measuredWidth

            titleView.layoutParams = LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                30, 0, Utils.px(menuWidth), 0
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
            orientation = LinearLayout.HORIZONTAL
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

            this.addView(itemView, LayoutHelper.createLinear(
                40, 40,
                if (itemsCount() != 0) 15 else 0, 0, 0, 0
            ))
        }

        private fun createItemView() : ImageView
        {
            return ImageView(context).apply {
                isClickable = true
                setOnTouchListener(InstantPressListener())

                background = Theme.createRectSelector(Theme.color_bigActionBar_item_bg, FloatArray(4).apply {
                    fill(Utils.dp(10F))
                }, true)

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