package tv.ridal.UI.Activities.SettingsActivity

import android.content.Context
import androidx.fragment.app.Fragment
import tv.ridal.UI.Activities.SettingsActivity.SettingsActivity

open class BaseSettingsFragment : Fragment()
{
    override fun getContext(): Context {
        return SettingsActivity.instance()
    }
}