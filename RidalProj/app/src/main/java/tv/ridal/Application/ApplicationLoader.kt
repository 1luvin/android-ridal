package tv.ridal.Application

import android.app.Application
import android.content.Context

class ApplicationLoader : Application()
{
    companion object
    {
        const val APP_NAME = "Ridal"

        const val WEBSITE_URL = "https://ridal.tv"

        @Volatile
        private var INSTANCE: ApplicationLoader? = null

        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApplicationLoader().also {
                    INSTANCE = it
                }
            }

    }

    override fun onCreate()
    {
        super.onCreate()

        INSTANCE = this

        Theme.setTheme(Theme.LIGHT)
        Locale.setLocale(Locale.RU)
    }
}


































//