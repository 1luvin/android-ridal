package tv.ridal

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentContainerView
import tv.ridal.UI.Layout.LayoutHelper

class SettingsActivity : BaseActivity()
{

    private lateinit var rootLayout: FrameLayout
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootLayout = FrameLayout(this).apply {
            layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            )
        }
        setContentView(rootLayout)

        fragmentContainer = FragmentContainerView(this).apply {
            id = View.generateViewId()
        }

        rootLayout.apply {
            addView(fragmentContainer, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            ))
        }

        supportFragmentManager.beginTransaction().add(
            fragmentContainer.id, SettingsFragment.newInstance()
        ).commit()
    }

}






































//