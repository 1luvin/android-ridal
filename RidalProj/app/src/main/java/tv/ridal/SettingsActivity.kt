package tv.ridal

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentContainerView
import tv.ridal.util.Theme
import tv.ridal.ui.layout.Layout
import tv.ridal.util.Utils

class SettingsActivity : BaseActivity()
{
    companion object
    {
        @Volatile
        private var INSTANCE: SettingsActivity? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsActivity().also {
                    INSTANCE = it
                }
            }
    }

    private lateinit var rootLayout: FrameLayout
    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        INSTANCE = this

        rootLayout = FrameLayout(this)
        setContentView(rootLayout)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->

            val insets = windowInsets.getInsets( WindowInsetsCompat.Type.navigationBars() )

            rootLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            WindowInsetsCompat.CONSUMED
        }

        setupStatusBar()

        fragmentContainer = FragmentContainerView(this).apply {
            id = View.generateViewId()
        }

        rootLayout.apply {
            addView(fragmentContainer, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            ))
        }

        supportFragmentManager.beginTransaction().add(
            fragmentContainer.id, SettingsFragment.instance()
        ).commit()
    }

    private fun setupStatusBar()
    {
        Utils.enableDarkStatusBar(window, ! Theme.isDark())
    }

}






































//