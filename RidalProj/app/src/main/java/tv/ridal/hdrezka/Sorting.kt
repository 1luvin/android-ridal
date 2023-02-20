package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale
import kotlin.collections.HashMap

class Sorting {

    companion object {

        private const val url_base = "?filter="

        private var sortings = HashMap<String, String>().apply {
            put(Locale.string(R.string.sorting_last), "last")
            put(Locale.string(R.string.sorting_popular), "popular")
            put(Locale.string(R.string.sorting_watching), "watching")
        }

        fun url(sorting: String?): String = sortings[sorting]?.let { "$url_base$it" } ?: ""
    }
}