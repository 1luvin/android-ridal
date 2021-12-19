package tv.ridal

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tunjid.androidx.navigation.Navigator

open class BaseFragment : Fragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "${View.generateViewId()}"

    override fun getContext(): Context {
        return ApplicationActivity.instance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    protected fun startFragment(fragment: BaseFragment)
    {
        ApplicationActivity.instance().multiStackNavigator.push(fragment)
    }

    protected fun finish()
    {
        ApplicationActivity.instance().multiStackNavigator.pop()
    }
}