package tv.ridal.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.AppActivity
import tv.ridal.CatalogFragment
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.view.MovieView
import tv.ridal.hdrezka.Movie

class MoviesAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>()
{
    private var onMovieClick: ((Movie) -> Unit)? = null
    fun onMovieClick(l: ((Movie) -> Unit)?)
    {
        onMovieClick = l
    }

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
                current.posterUrl?.let {
                    this.posterUrl = it
                }

                movieName = current.name

                detailText = if (current.rating != null) {
                    current.rating!!
                } else {
                    current.type!!
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder
    {
        val movieView = MovieView(parent.context)

        if ( AppActivity.currentFragment() is CatalogFragment )
        {
            movieView.layoutParams = Layout.ezRecycler(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT
            )
        }

        return ViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind( movies[position] )
    }

    override fun getItemCount(): Int = movies.size
}





































//