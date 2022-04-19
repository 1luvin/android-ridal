package tv.ridal

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.view.ClearableInputView
import tv.ridal.ui.view.InputBar
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class SearchInputFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "SearchInputFragment"


    private lateinit var rootFrame: FrameLayout
    private lateinit var inputBar: InputBar
    private lateinit var scroll: NestedScrollView
    private lateinit var layout: VLinearLayout


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return rootFrame
    }

    private fun createUI()
    {
        inputBar = InputBar(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)

            setBackgroundColor(
                Theme.overlayColor( Theme.color_bg, 0.04F )
            )

            measure(0, 0)
        }
        layout = VLinearLayout(context)

        scroll = NestedScrollView(context).apply {
            addView(layout)
        }

        rootFrame = FrameLayout(context).apply {
            setBackgroundColor( Theme.color(Theme.color_bg) )

            addView(inputBar, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))

            addView(scroll, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.TOP,
                0, inputBar.measuredHeight, 0, 0
            ))
        }

    }
}


































//