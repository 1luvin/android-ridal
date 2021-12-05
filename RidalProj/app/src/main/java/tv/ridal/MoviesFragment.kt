package tv.ridal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tunjid.androidx.navigation.Navigator

class MoviesFragment : BaseFragment(), Navigator.TagProvider
{
    override val stableTag: String
        get() = "MoviesFragment${View.generateViewId()}"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


}





































//