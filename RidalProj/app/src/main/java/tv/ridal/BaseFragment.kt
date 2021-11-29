package tv.ridal

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

open class BaseFragment : Fragment()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        parentFragmentManager.commit {
//            setCustomAnimations(
//                R.anim.zoom_in,
//                R.anim.zoom_out,
//                R.anim.zoom_pop_in,
//                R.anim.zoom_pop_out
//            )
//        }
    }
}