package tv.ridal.Application

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import tv.ridal.Application.UserData.User

class ApplicationLoader : Application()
{
    companion object
    {
        const val APP_NAME = "Ridal"

        const val WEBSITE = "https://ridal.tv"

        @Volatile
        private var INSTANCE: ApplicationLoader? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApplicationLoader().also {
                    INSTANCE = it
                }
            }
    }

    // volley

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext)
    }

    override fun onCreate()
    {
        super.onCreate()

        

        INSTANCE = this

        Theme.setTheme(Theme.DARK)
        Locale.setLocale(Locale.RU)
    }

}


































//