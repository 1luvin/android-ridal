package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale
import kotlin.collections.HashMap

class Sorting
{
    companion object
    {
        const val url_base = "?filter="

        private var sortingUrls = HashMap<String, String>().apply {
            this[Locale.string(R.string.sorting_last)] = "last"
            this[Locale.string(R.string.sorting_popular)] = "popular"
            this[Locale.string(R.string.sorting_watching)] = "watching"
        }

        fun url(sorting: String) : String
        {
            var url = ""
            if ( sortingUrls.containsKey(sorting) )
            {
                url += "${url_base}${sortingUrls[sorting]}"
            }

            return url
        }
    }
}