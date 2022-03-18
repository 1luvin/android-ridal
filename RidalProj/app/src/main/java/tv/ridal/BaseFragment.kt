package tv.ridal

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tunjid.androidx.navigation.Navigator
import kotlin.random.Random

open class BaseFragment : Fragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "${Random.nextInt()}"

    override fun getContext(): Context = ApplicationActivity.instance()

    // Фрагменты
    protected fun startFragment(fragment: BaseFragment) = ApplicationActivity.instance().multiStackNavigator.push(fragment)
    protected fun finish() = ApplicationActivity.instance().multiStackNavigator.pop()
}







































//