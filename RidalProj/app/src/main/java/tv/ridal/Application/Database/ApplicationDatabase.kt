package tv.ridal.Application.Database

import androidx.room.*
import androidx.room.Database
import tv.ridal.Application.ApplicationLoader


@Database(entities = [tv.ridal.Application.Database.Database.Folder::class, ], version = 1, exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase()
{
    companion object
    {
        @Volatile
        private var INSTANCE: ApplicationDatabase? = null

        fun instance() =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    ApplicationLoader.instance().applicationContext,
                    ApplicationDatabase::class.java,
                    "ridal-db"
                ).build().also {
                    INSTANCE = it
                }
            }
    }
}





































//