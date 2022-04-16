package tv.ridal.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.AppActivity
import tv.ridal.CatalogFragment
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.view.MovieView
import tv.ridal.hdrezka.Movie
import tv.ridal.SearchFragment

class MoviesAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>()
{
    private var onMovieClick: ((Movie) -> Unit)? = null
    fun onMovieClick(l: ((Movie) -> Unit))
    {
        onMovieClick = l
    }

    var hasViewAll: Boolean = false

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        init
        {
            val movieView = itemView as MovieView
            movieView.apply {
                setOnClickListener {
                    onMovieClick?.invoke( movies[adapterPosition] )
                }
            }
        }

        fun bind(current: Movie)
        {
            val movieView = itemView as MovieView
            movieView.apply {
                posterUrl = current.posterUrl
                movieName = current.name
                movieType = current.type.ruType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val movieView = MovieView(parent.context)

        if (AppActivity.currentFragment() is CatalogFragment)
        {
            movieView.layoutParams = RecyclerView.LayoutParams(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT
            )
        }
        else if (AppActivity.currentFragment() is SearchFragment)
        {
            movieView.layoutParams = RecyclerView.LayoutParams(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT
            )
        }

        return ViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        if (hasViewAll && position == itemCount - 1)
        {

        }
        else
        {
            holder.bind( movies[position] )
        }
    }

    override fun getItemCount(): Int
    {
        var count = movies.size
        if (hasViewAll) count++

        return count
    }
}





































//