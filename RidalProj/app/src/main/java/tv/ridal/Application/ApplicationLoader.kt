package tv.ridal.Application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class ApplicationLoader : Application()
{
    companion object
    {
        const val APP_NAME = "Ridal"
        const val WEBSITE = "https://ridal.tv"

        const val SETTINGS_PREF = "tv.ridal.settings"

        @Volatile
        private var INSTANCE: ApplicationLoader? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApplicationLoader().also {
                    INSTANCE = it
                }
            }
    }

    // Volley's RequestQueue
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext)
    }

    // Shared Preferences
    val settingsPref: SharedPreferences by lazy {
        getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE)
    }

    val configuration: Configuration by lazy {
        resources.configuration
    }

    override fun onCreate()
    {
        super.onCreate()

        INSTANCE = this

        Theme.initMainColor()
        Theme.initTheme()

        Locale.setLocale(Locale.RU)

    }



}


































//