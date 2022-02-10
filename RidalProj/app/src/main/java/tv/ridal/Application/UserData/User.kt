package tv.ridal.Application.UserData

import kotlinx.serialization.Serializable

class User
{
    companion object
    {
        @Serializable
        lateinit var settings: Settings
        fun createSettings() {
            settings = Settings()
        }
    }

    @Serializable
    class Settings
    {
        var theme_isDark: Boolean = false
        var theme_mainColor: Int = 0
    }
}





































//