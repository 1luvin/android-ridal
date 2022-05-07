package tv.ridal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import tv.ridal.util.Theme

class App : Application()
{
    companion object
    {
        const val SETTINGS_PREF = "tv.ridal.settings"

        @Volatile
        private var INSTANCE: App? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: App().also {
                    INSTANCE = it
                }
            }

        val appContext: Context get() = instance().applicationContext
    }

    // Volley's RequestQueue
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue( appContext )
    }

    // Shared Preferences
    val settingsPref: SharedPreferences by lazy {
        getSharedPreferences( SETTINGS_PREF, Context.MODE_PRIVATE )
    }

    val configuration: Configuration by lazy {
        resources.configuration
    }

    override fun onCreate()
    {
        super.onCreate()

        INSTANCE = this

        Theme.initTheme()
    }

}


































//