package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale

class Section
{
    companion object
    {
        const val url_base = "&genre="

        private var sections = HashMap<String, String>().apply {
            this[Locale.string(R.string.allSections)] = ""
            this[Locale.string(R.string.films)] = "1"
            this[Locale.string(R.string.series)] = "2"
            this[Locale.string(R.string.cartoons)] = "3"
            this[Locale.string(R.string.anime)] = "82"
        }

        fun url(section: String?) : String
        {
            if ( section == null ) return ""

            if ( sections.containsKey(section) )
            {
                return "${url_base}${sections[section]}"
            }

            return ""
        }
    }
}