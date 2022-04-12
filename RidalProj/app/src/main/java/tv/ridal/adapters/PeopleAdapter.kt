package tv.ridal.adapters

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.hdrezka.Movie
import tv.ridal.ui.view.PersonView


class PeopleAdapter(private val people: ArrayList<Movie.Person>) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>()
{
    private var onPersonClick: ((Drawable) -> Unit)? = null
    fun onPersonClick(l: (Drawable) -> Unit)
    {
        onPersonClick = l
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        init
        {
            val personView = itemView as PersonView
            personView.apply {
                setOnClickListener {
                    onPersonClick?.invoke( photo!! )
                }
            }
        }

        fun bind(current: Movie.Person)
        {
            val personView = itemView as PersonView
            personView.apply {
                setPerson(current)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleAdapter.ViewHolder
    {
        val personView = PersonView(parent.context)

        return ViewHolder(personView)
    }

    override fun onBindViewHolder(holder: PeopleAdapter.ViewHolder, position: Int)
    {
        holder.bind( people[position] )
    }

    override fun getItemCount(): Int = people.size
}





































//