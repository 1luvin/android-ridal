package tv.ridal.Adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.Components.MovieView
import tv.ridal.HDRezka.Movie

class MoviesAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>()
{
    var onMovieClick: ((Movie) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        init {
            itemView.setOnClickListener {
                onMovieClick?.invoke(movies[adapterPosition])
            }
        }

        fun bind(current: Movie)
        {
            val movieView = itemView as MovieView
            movieView.apply {
                posterUrl = current.posterUrl
                movieName = current.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(MovieView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
       holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

}





































//