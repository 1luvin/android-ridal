package tv.ridal.Application.UserData

import kotlinx.serialization.Serializable
import tv.ridal.Application.ApplicationLoader
import java.io.*

class USER
{
    companion object
    {
        @Serializable
        var settings: Settings? = null

        const val settingsFileName = "user.settings"

        fun hasSettings() : Boolean
        {
            return File(ApplicationLoader.instance().filesDir, settingsFileName).exists() && settings != null
        }

        fun checkSettings()
        {
            val settinsFile = File(ApplicationLoader.instance().filesDir, settingsFileName)
            if (settinsFile.exists())
            {
                val fis = FileInputStream(settinsFile)
                val ois = ObjectInputStream(fis)

                settings = ois.readObject() as Settings

                ois.close()
            }
            else
            {
                settings = Settings()

                settinsFile.parentFile.mkdirs()

                updateSettings()
            }
        }

        fun updateSettings()
        {
            if ( ! hasSettings()) return

            val fos = FileOutputStream(settingsFileName)
            val oos = ObjectOutputStream(fos)

            oos.writeObject(settings)

            oos.close()
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