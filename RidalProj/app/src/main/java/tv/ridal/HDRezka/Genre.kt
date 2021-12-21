package tv.ridal.HDRezka

import tv.ridal.Application.Locale

class Genre
{
    companion object
    {
        fun createGenres(section: String) : ArrayList<String>
        {
            var genres = ArrayList<String>().apply {
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
    }
}