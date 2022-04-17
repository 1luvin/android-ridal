package tv.ridal

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseSettingsFragment : Fragment()
{
    override fun getContext(): Context  = SettingsActivity.instance()
}