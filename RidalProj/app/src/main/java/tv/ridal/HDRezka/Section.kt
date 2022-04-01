package tv.ridal.HDRezka

import tv.ridal.Application.Locale

class Section
{
    companion object
    {
        const val url_base = "&genre="

        private var sectionUrls = HashMap<String, String>().apply {
            this[Locale.text(Locale.text_allSections)] = ""
            this[Locale.text(Locale.text_films)] = "1"
            this[Locale.text(Locale.text_series)] = "2"
            this[Locale.text(Locale.text_cartoons)] = "3"
            this[Locale.text(Locale.text_anime)] = "82"
        }

        fun url(sortingKey: String) : String
        {
            if (sortingKey == Locale.text(Locale.text_allSections)) return ""

            var url = ""
            if ( sectionUrls.containsKey(sortingKey) ) {
                url += url_base
                url += sectionUrls[sortingKey]
            }

            return url
        }
    }
}