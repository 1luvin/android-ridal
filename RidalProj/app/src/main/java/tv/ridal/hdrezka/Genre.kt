package tv.ridal.hdrezka

import tv.ridal.R
import tv.ridal.util.Locale

class Genre
{
    companion object
    {
        fun createGenres(section: String) : LinkedHashMap<String, String>
        {
            val genres = LinkedHashMap<String, String>().apply {
                put( Locale.string(R.string.allGenres), "" )
                put( Locale.string(R.string.genre_fiction), "fiction/" )
                put( Locale.string(R.string.genre_horror), "horror/")
                put( Locale.string(R.string.genre_adventures), "adventures/" )
                put( Locale.string(R.string.genre_comedy), "comedy/" )
                put( Locale.string(R.string.genre_fantasy), "fantasy/" )
                put( Locale.string(R.string.genre_drama), "drama/" )
                put( Locale.string(R.string.genre_sport), "sport/" )
                put( Locale.string(R.string.genre_thriller), "thriller/" )
            }

            when (section)
            {
                Locale.string(R.string.films) ->
                {
                    genres.apply {
                        put( Locale.string(R.string.genre_western), "western/" )
                        put( Locale.string(R.string.genre_arthouse), "arthouse/" )
                        put( Locale.string(R.string.genre_crime), "crime/" )
                        put( Locale.string(R.string.genre_erotic), "erotic/" )
                        put( Locale.string(R.string.genre_theatre), "theatre/" )
                        put( Locale.string(R.string.genre_our), "our/" )
                        put( Locale.string(R.string.genre_family), "family/" )
                        put( Locale.string(R.string.genre_action), "action/" )
                        put( Locale.string(R.string.genre_musical), "musical/" )
                        put( Locale.string(R.string.genre_kids), "kids/" )
                        put( Locale.string(R.string.genre_concert), "concert/" )
                        put( Locale.string(R.string.genre_ukrainian), "ukrainian/" )
                        put( Locale.string(R.string.genre_military), "military/" )
                        put( Locale.string(R.string.genre_melodrama), "melodrama/" )
                        put( Locale.string(R.string.genre_historical), "historical/" )
                        put( Locale.string(R.string.genre_travel), "travel/" )
                        put( Locale.string(R.string.genre_standup), "standup/" )
                        put( Locale.string(R.string.genre_foreign), "foreign/" )
                        put( Locale.string(R.string.genre_biographical), "biographical/" )
                        put( Locale.string(R.string.genre_detective), "detective/" )
                        put( Locale.string(R.string.genre_documentary), "documentary/" )
                        put( Locale.string(R.string.genre_cognitive), "cognitive/" )
                        put( Locale.string(R.string.genre_short), "short/" )
                    }
                }
                Locale.string(R.string.series) ->
                {
                    genres.apply {
                        put( Locale.string(R.string.genre_western), "western/" )
                        put( Locale.string(R.string.genre_arthouse), "arthouse/" )
                        put( Locale.string(R.string.genre_crime), "crime/" )
                        put( Locale.string(R.string.genre_erotic), "erotic/" )
                        put( Locale.string(R.string.genre_family), "family/" )
                        put( Locale.string(R.string.genre_action), "action/" )
                        put( Locale.string(R.string.genre_musical2), "musical/" )
                        put( Locale.string(R.string.genre_ukrainian), "ukrainian/" )
                        put( Locale.string(R.string.genre_military), "military/" )
                        put( Locale.string(R.string.genre_melodrama), "melodrama/" )
                        put( Locale.string(R.string.genre_historical), "historical/" )
                        put( Locale.string(R.string.genre_standup), "standup/" )
                        put( Locale.string(R.string.genre_biographical), "biographical/" )
                        put( Locale.string(R.string.genre_detective), "detective/" )
                        put( Locale.string(R.string.genre_documentary), "documentary/" )

                        put( Locale.string(R.string.genre_realtv), "realtv/" )
                        put( Locale.string(R.string.genre_russian), "russian/" )
                        put( Locale.string(R.string.genre_telecasts), "telecasts/" )
                    }
                }
                Locale.string(R.string.cartoons) ->
                {
                    genres.apply {
                        put( Locale.string(R.string.genre_our), "our/" )
                        put( Locale.string(R.string.genre_family), "family/" )
                        put( Locale.string(R.string.genre_musical), "musical/" )
                        put( Locale.string(R.string.genre_kids), "kids/" )
                        put( Locale.string(R.string.genre_foreign), "foreign/" )
                        put( Locale.string(R.string.genre_cognitive), "cognitive/" )

                        put( Locale.string(R.string.genre_multseries), "multseries/" )
                        put( Locale.string(R.string.genre_fairytale), "fairytale/" )
                        put( Locale.string(R.string.genre_adult), "adult/" )
                        put( Locale.string(R.string.genre_fullLength), "full-length/" )
                        put( Locale.string(R.string.genre_soyzmyltfilm), "soyzmyltfilm/" )
                    }
                }
                Locale.string(R.string.anime) ->
                {
                    genres.apply {
                        put( Locale.string(R.string.genre_erotic), "erotic/" )
                        put( Locale.string(R.string.genre_action), "action/" )
                        put( Locale.string(R.string.genre_musical2), "musical/" )
                        put( Locale.string(R.string.genre_military), "military/" )
                        put( Locale.string(R.string.genre_historical), "historical/" )
                        put( Locale.string(R.string.genre_detective), "detective/" )

                        put( Locale.string(R.string.genre_romance), "romance/" )
                        put( Locale.string(R.string.genre_samurai), "samurai/" )
                        put( Locale.string(R.string.genre_parody), "parody/" )
                        put( Locale.string(R.string.genre_kodomo), "kodomo/" )
                        put( Locale.string(R.string.genre_shounenai), "shounenai/" )
                        put( Locale.string(R.string.genre_school), "school/" )
                        put( Locale.string(R.string.genre_shoujoai), "shoujoai/" )
                        put( Locale.string(R.string.genre_ecchi), "ecchi/" )
                        put( Locale.string(R.string.genre_shoujo), "shoujo/" )
                        put( Locale.string(R.string.genre_mahoushoujo), "mahoushoujo/" )
                        put( Locale.string(R.string.genre_mystery), "mystery/" )
                        put( Locale.string(R.string.genre_fighting), "fighting/" )
                        put( Locale.string(R.string.genre_everyday), "everyday/" )
                        put( Locale.string(R.string.genre_shounen), "shounen/" )
                        put( Locale.string(R.string.genre_mecha), "mecha/" )
                    }
                }
            }

            return genres
        }

        fun url(genres: LinkedHashMap<String, String>?, genre: String?) : String
        {
            if ( genres == null && genre == null ) return ""

            if ( genres!!.containsKey(genre) )
            {
                return genres[genre]!!
            }

            return ""
        }
    }
}





































//