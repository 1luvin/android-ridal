package tv.ridal.ui.popup

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.style.Pulse
import tv.ridal.util.Theme
import tv.ridal.ui.layout.Layout
import tv.ridal.util.Utils

class LoadingPopup(context: Context) : Popup(context)
{
    private lateinit var contentView: FrameLayout
    private lateinit var loadingView: ProgressBar

    init
    {
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
            addView(loadingView, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.CENTER
            ))
        }

        setContentView(contentView, Layout.ezFrame(
            120, 120,
            Gravity.CENTER
        ))
    }
}





































//