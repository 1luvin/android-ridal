package tv.ridal.UI.Popup

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.style.Pulse
import tv.ridal.Application.Theme
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.Application.Utils

class LoadingPopup(context: Context) : Popup(context)
{
    private lateinit var contentView: FrameLayout
    private lateinit var loadingView: ProgressBar

    init
    {
        window!!.decorView.setBackgroundResource(android.R.color.transparent)

        createUI()
    }

    private fun createUI()
    {
        contentView = FrameLayout(context).apply {
            background = Theme.rect(
                Theme.color_bg,
                radii = FloatArray(4).apply {
                    fill(Utils.dp(20F))
                }
            )
        }

        loadingView = ProgressBar(context).apply {
            indeterminateDrawable = Pulse().apply {
                color = Theme.color(Theme.color_main)
            }
        }

        contentView.apply {
            addView(loadingView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER
            ))
        }

        setContentView(contentView, LayoutHelper.createFrame(
            120, 120,
            Gravity.CENTER
        ))
    }

}





































//