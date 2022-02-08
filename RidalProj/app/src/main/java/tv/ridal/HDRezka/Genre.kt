package tv.ridal.HDRezka

import tv.ridal.Application.Locale

class Genre
{
    companion object
    {
        fun createGenres(section: String) : ArrayList<String>
        {
            var genres = ArrayList<String>().apply {
                ensureCapacity(20) // минимальное количество фильтров (категория Мультфильмы)

                add(Locale.text(Locale.text_allGenres))
                add(Locale.text(Locale.genre_fiction))
                add(Locale.text(Locale.genre_horror))
                add(Locale.text(Locale.genre_adventures))
                add(Locale.text(Locale.genre_comedy))
                add(Locale.text(Locale.genre_fantasy))
                add(Locale.text(Locale.genre_drama))
                add(Locale.text(Locale.genre_sport))
                add(Locale.text(Locale.genre_thriller))
            }

            when (section)
            {
                Locale.text(Locale.text_films) ->
                {
                    genres.add(Locale.text(Locale.genre_western))
                    genres.add(Locale.text(Locale.genre_arthouse))
                    genres.add(Locale.text(Locale.genre_crime))
                    genres.add(Locale.text(Locale.genre_erotic))
                    genres.add(Locale.text(Locale.genre_theatre))
                    genres.add(Locale.text(Locale.genre_our))
                    genres.add(Locale.text(Locale.genre_family))
                    genres.add(Locale.text(Locale.genre_action))
                    genres.add(Locale.text(Locale.genre_musical))
                    genres.add(Locale.text(Locale.genre_kids))
                    genres.add(Locale.text(Locale.genre_concert))
                    genres.add(Locale.text(Locale.genre_ukrainian))
                    genres.add(Locale.text(Locale.genre_military))
                    genres.add(Locale.text(Locale.genre_melodrama))
                    genres.add(Locale.text(Locale.genre_historical))
                    genres.add(Locale.text(Locale.genre_travel))
                    genres.add(Locale.text(Locale.genre_standup))
                    genres.add(Locale.text(Locale.genre_foreign))
                    genres.add(Locale.text(Locale.genre_biographical))
                    genres.add(Locale.text(Locale.genre_detective))
                    genres.add(Locale.text(Locale.genre_documentary))
                    genres.add(Locale.text(Locale.genre_cognitive))
                    genres.add(Locale.text(Locale.genre_short))
                }
                Locale.text(Locale.text_series) ->
                {
                    genres.add(Locale.text(Locale.genre_western))
                    genres.add(Locale.text(Locale.genre_arthouse))
                    genres.add(Locale.text(Locale.genre_crime))
                    genres.add(Locale.text(Locale.genre_erotic))
                    genres.add(Locale.text(Locale.genre_family))
                    genres.add(Locale.text(Locale.genre_action))
                    genres.add(Locale.text(Locale.genre_musical2))
                    genres.add(Locale.text(Locale.genre_ukrainian))
                    genres.add(Locale.text(Locale.genre_military))
                    genres.add(Locale.text(Locale.genre_melodrama))
                    genres.add(Locale.text(Locale.genre_historical))
                    genres.add(Locale.text(Locale.genre_standup))
                    genres.add(Locale.text(Locale.genre_biographical))
                    genres.add(Locale.text(Locale.genre_detective))
                    genres.add(Locale.text(Locale.genre_documentary))

                    genres.add(Locale.text(Locale.genre_realtv))
                    genres.add(Locale.text(Locale.genre_russian))
                    genres.add(Locale.text(Locale.genre_telecasts))
                }
                Locale.text(Locale.text_cartoons) ->
                {
                    genres.add(Locale.text(Locale.genre_our))
                    genres.add(Locale.text(Locale.genre_family))
                    genres.add(Locale.text(Locale.genre_musical))
                    genres.add(Locale.text(Locale.genre_kids))
                    genres.add(Locale.text(Locale.genre_foreign))
                    genres.add(Locale.text(Locale.genre_cognitive))

                    genres.add(Locale.text(Locale.genre_multseries))
                    genres.add(Locale.text(Locale.genre_fairytale))
                    genres.add(Locale.text(Locale.genre_adult))
                    genres.add(Locale.text(Locale.genre_fullLength))
                    genres.add(Locale.text(Locale.genre_soyzmyltfilm))
                }
                Locale.text(Locale.text_anime) ->
                {
                    genres.add(Locale.text(Locale.genre_erotic))
                    genres.add(Locale.text(Locale.genre_action))
                    genres.add(Locale.text(Locale.genre_musical2))
                    genres.add(Locale.text(Locale.genre_military))
                    genres.add(Locale.text(Locale.genre_historical))
                    genres.add(Locale.text(Locale.genre_detective))

                    genres.add(Locale.text(Locale.genre_romance))
                    genres.add(Locale.text(Locale.genre_samurai))
                    genres.add(Locale.text(Locale.genre_parody))
                    genres.add(Locale.text(Locale.genre_kodomo))
                    genres.add(Locale.text(Locale.genre_shounenai))
                    genres.add(Locale.text(Locale.genre_school))
                    genres.add(Locale.text(Locale.genre_shoujoai))
                    genres.add(Locale.text(Locale.genre_ecchi))
                    genres.add(Locale.text(Locale.genre_shoujo))
                    genres.add(Locale.text(Locale.genre_mahoushoujo))
                    genres.add(Locale.text(Locale.genre_mystery))
                    genres.add(Locale.text(Locale.genre_fighting))
                    genres.add(Locale.text(Locale.genre_everyday))
                    genres.add(Locale.text(Locale.genre_shounen))
                    genres.add(Locale.text(Locale.genre_mecha))
                }
            }

            return genres
        }

        private fun fillGenreUrls()
        {
            genreUrls.apply {
                // Фильмы
                this[Locale.text(Locale.text_allGenres)] = ""

                this[Locale.text(Locale.genre_western)] = "western/"
                this[Locale.text(Locale.genre_arthouse)] = "arthouse/"
                this[Locale.text(Locale.genre_crime)] = "crime/"
                this[Locale.text(Locale.genre_fiction)] = "fiction/"
                this[Locale.text(Locale.genre_horror)] = "horror/"
                this[Locale.text(Locale.genre_erotic)] = "erotic/"
                this[Locale.text(Locale.genre_theatre)] = "theatre/"
                this[Locale.text(Locale.genre_our)] = "our/"
                this[Locale.text(Locale.genre_family)] = "family/"
                this[Locale.text(Locale.genre_action)] = "action/"
                this[Locale.text(Locale.genre_adventures)] = "adventures/"
                this[Locale.text(Locale.genre_comedy)] = "comedy/"
                this[Locale.text(Locale.genre_musical)] = "musical/"
                this[Locale.text(Locale.genre_kids)] = "kids/"
                this[Locale.text(Locale.genre_concert)] = "concert/"
                this[Locale.text(Locale.genre_ukrainian)] = "gukrainian/"
                this[Locale.text(Locale.genre_fantasy)] = "fantasy/"
                this[Locale.text(Locale.genre_military)] = "military/"
                this[Locale.text(Locale.genre_drama)] = "drama/"
                this[Locale.text(Locale.genre_melodrama)] = "melodrama/"
                this[Locale.text(Locale.genre_historical)] = "historical/"
                this[Locale.text(Locale.genre_travel)] = "travel/"
                this[Locale.text(Locale.genre_standup)] = "standup/"
                this[Locale.text(Locale.genre_foreign)] = "foreign/"
                this[Locale.text(Locale.genre_biographical)] = "biographical/"
                this[Locale.text(Locale.genre_detective)] = "detective/"
                this[Locale.text(Locale.genre_sport)] = "sport/"
                this[Locale.text(Locale.genre_thriller)] = "thriller/"
                this[Locale.text(Locale.genre_documentary)] = "documentary/"
                this[Locale.text(Locale.genre_cognitive)] = "cognitive/"
                this[Locale.text(Locale.genre_short)] = "short/"

                // Сериалы
                this[Locale.text(Locale.genre_realtv)] = "realtv/"
                this[Locale.text(Locale.genre_russian)] = "russian/"
                this[Locale.text(Locale.genre_telecasts)] = "telecasts/"
                this[Locale.text(Locale.genre_musical2)] = "musical2/"

                // Мультфильмы
                this[Locale.text(Locale.genre_multseries)] = "multseries/"
                this[Locale.text(Locale.genre_fairytale)] = "fairytale/"
                this[Locale.text(Locale.genre_adult)] = "adult/"
                //const val genre_animation = "genre_animation"
                this[Locale.text(Locale.genre_fullLength)] = "fullLength/"
                this[Locale.text(Locale.genre_soyzmyltfilm)] = "soyzmyltfilm/"

                // Аниме
                this[Locale.text(Locale.genre_romance)] = "romance/"
                this[Locale.text(Locale.genre_samurai)] = "samurai/"
                this[Locale.text(Locale.genre_parody)] = "parody/"
                this[Locale.text(Locale.genre_kodomo)] = "kodomo/"
                this[Locale.text(Locale.genre_shounenai)] = "shounenai/"
                this[Locale.text(Locale.genre_school)] = "school/"
                this[Locale.text(Locale.genre_shoujoai)] = "shoujoai/"
                this[Locale.text(Locale.genre_ecchi)] = "ecchi/"
                this[Locale.text(Locale.genre_shoujo)] = "shoujo/"
                this[Locale.text(Locale.genre_mahoushoujo)] = "mahoushoujo/"
                this[Locale.text(Locale.genre_mystery)] = "mystery/"
                this[Locale.text(Locale.genre_fighting)] = "fighting/"
                this[Locale.text(Locale.genre_everyday)] = "everyday/"
                this[Locale.text(Locale.genre_shounen)] = "shounen/"
                this[Locale.text(Locale.genre_mecha)] = "mecha/"
            }
        }

        private var genreUrls = HashMap<String, String>()

        fun url(genreKey: String) : String
        {
            if ( genreUrls.isEmpty() ) fillGenreUrls()

            return genreUrls[genreKey] ?: "bad_genre_key"
        }
    }
}





































//