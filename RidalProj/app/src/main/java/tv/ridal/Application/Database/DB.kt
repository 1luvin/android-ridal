package tv.ridal.Application.Database

import androidx.room.*
import java.util.*

object DB
{

//    @Entity(tableName = "folders")
//    data class Folder(
//        @PrimaryKey val folderName: String,
//        val size: Int,
//        val creationDate: Date,
//        val modifiedDate: Date,
//    )
//    {
//        fun isEmpty(): Boolean
//        {
//            return size == 0
//        }
//    }
//
//    @Dao
//    interface FolderDao
//    {
//        @Insert(onConflict = OnConflictStrategy.IGNORE)
//        fun insert(folder: Folder)
//
//        @Query("SELECT folderName FROM folders")
//        fun allNames(): List<String>
//
//        @Query("SELECT COUNT(folderName) FROM folders")
//        fun count(): Int
//
//        @Query("SELECT * FROM folders")
//        fun allFolders(): List<Folder>
//    }
//
//    @Entity(tableName = "movies")
//    data class Movie(
//        @PrimaryKey
//        val movieUrl: String,
//    )
//
//    @Entity(primaryKeys = ["folderName", "movieUrl"])
//    data class FolderMovieCrossRef(
//        val folderName: String,
//        val movieUrl: String
//    )
//
//    data class FolderWithMovies(
//        @Embedded val folder: Folder,
//        @Relation(
//            parentColumn = "folderName",
//            entityColumn = "movieUrl",
//            associateBy = Junction(FolderMovieCrossRef::class)
//        )
//        val movies: List<Movie>
//    )
}