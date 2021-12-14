package tv.ridal.Application

class Locale
{
    companion object
    {
        const val RU: Int = 0

        fun setLocale(locale: Int)
        {
            when (locale)
            {
                RU ->
                {
                    texts[text_sett] = "Настройки"
                    texts[text_visualAppearance] = "Внешний вид"
                    texts[text_darkTheme] = "Темная тема"

                    texts[text_catalog] = "Каталог"
                    texts[text_collections] = "Подборки"
                    texts[text_search] = "Поиск"

                    texts[hint_search] = "Фильм, сериал, мультфильм, аниме"

                    texts[share_application] = "Привет, я использую ${ApplicationLoader.APP_NAME} для просмотра кино. " +
                            "Присоеденяйся! Скачать его можно здесь: ${ApplicationLoader.WEBSITE}"

                    texts[text_films] = "Фильмы"
                    texts[text_series] = "Сериалы"
                    texts[text_cartoons] = "Мультфильмы"
                    texts[text_anime] = "Аниме"

                    texts[text_favourites] = "Избранное"
                    texts[text_empty] = "Пусто"
                }
            }
        }

        /*
            Ключи для строк
         */

        const val text_sett = "text_sett"
        const val text_visualAppearance = "text_visualAppearance"
        const val text_darkTheme = "text_darkTheme"

        const val text_catalog = "text_catalog"
        const val text_collections = "text_collections"
        const val text_search = "text_search"

        const val hint_search = "hint_search"

        const val share_application = "share_application"

        const val text_films = "text_films"
        const val text_series = "text_series"
        const val text_cartoons = "text_cartoons"
        const val text_anime = "text_anime"

        const val text_favourites = "text_favourites"
        const val text_empty = "text_empty"

        private var texts = HashMap<String, String>()

        fun text(textKey: String) : String
        {
            return texts[textKey] ?: "bad_text_key"
        }
    }
}


































//