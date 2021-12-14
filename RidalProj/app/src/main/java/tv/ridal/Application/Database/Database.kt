package tv.ridal.Application.Database

import androidx.room.*
import java.util.*

object Database
{

    @Entity(tableName = "folders")
    data class Folder(
        @PrimaryKey val folderName: String,
        val size: Int,
        val creationDate: Date,
        val modifiedDate: Date,
    )

    @Entity(tableName = "movies")
    data class Movie(
        @PrimaryKey
        val movieUrl: String,
    )

    @Entity(primaryKeys = ["folderName", "movieUrl"])
    data class FolderMovieCrossRef(
        val folderName: String,
        val movieUrl: String
    )

    data class FolderWithMovies(
        @Embedded val folder: Folder,
        @Relation(
            parentColumn = "folderName",
            entityColumn = "movieUrl",
            associateBy = Junction(FolderMovieCrossRef::class)
        )
        val movies: List<Movie>
    )

}





































//