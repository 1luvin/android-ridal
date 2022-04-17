package tv.ridal.util

import tv.ridal.App

class Locale
{
    companion object
    {

        fun string(key: Int) : String = App.appContext.resources.getString(key)

    }
}


































//