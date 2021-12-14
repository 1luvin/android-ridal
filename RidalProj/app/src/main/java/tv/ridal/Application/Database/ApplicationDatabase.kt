package tv.ridal.Application.Database

import androidx.room.*
import androidx.room.Database
import tv.ridal.Application.ApplicationLoader


@Database(entities = [tv.ridal.Application.Database.Database.Folder::class, ], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ApplicationDatabase : RoomDatabase()
{

    abstract fun folderDao(): tv.ridal.Application.Database.Database.FolderDao

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