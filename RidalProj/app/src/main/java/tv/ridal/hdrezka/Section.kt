package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale

class Section
{
    companion object
    {
        const val url_base = "&genre="

        private var sectionUrls = HashMap<String, String>().apply {
            this[Locale.string(R.string.films)] = "1"
            this[Locale.string(R.string.series)] = "2"
            this[Locale.string(R.string.cartoons)] = "3"
            this[Locale.string(R.string.anime)] = "82"
        }

        fun url(sorting: String) : String
        {
            if (sorting == Locale.string(R.string.allSections)) return ""

            var url = ""
            if ( sectionUrls.containsKey(sorting) )
            {
                url += "${url_base}${sectionUrls[sorting]}"
            }

            return url
        }
    }
}