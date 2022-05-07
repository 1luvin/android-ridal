package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale
import kotlin.collections.HashMap

class Sorting
{
    companion object
    {
        private const val url_base = "?filter="

        private var sortings = HashMap<String, String>().apply {
            this[Locale.string(R.string.sorting_last)] = "last"
            this[Locale.string(R.string.sorting_popular)] = "popular"
            this[Locale.string(R.string.sorting_watching)] = "watching"
        }

        fun url(sorting: String?) : String
        {
            if ( sorting == null ) return ""

            if ( sortings.containsKey(sorting) )
            {
                return "${url_base}${sortings[sorting]}"
            }

            return ""
        }

    }
}