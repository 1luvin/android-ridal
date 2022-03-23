package tv.ridal

import android.content.Context
import androidx.fragment.app.Fragment
import com.tunjid.androidx.navigation.Navigator
import kotlin.random.Random

open class BaseAppFragment : Fragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "${Random.nextInt()}"

    override fun getContext(): Context = AppActivity.instance()

    // Фрагменты
    protected fun startFragment(fragment: BaseAppFragment) = AppActivity.instance().multiStackNavigator.push(fragment)
    protected fun finish() = AppActivity.instance().multiStackNavigator.pop()
}







































//