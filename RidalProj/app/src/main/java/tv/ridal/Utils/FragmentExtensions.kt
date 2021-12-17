package tv.ridal.Utils

import androidx.fragment.app.Fragment
import tv.ridal.MoviesFragment


fun Fragment.withFilters(action: MoviesFragment.FiltersBottomPopupFragment.() -> Unit) {
    parentFragment
        ?.let {
            it as? MoviesFragment.FiltersBottomPopupFragment
        }
        ?.also {
            action.invoke(it)
        }
}
