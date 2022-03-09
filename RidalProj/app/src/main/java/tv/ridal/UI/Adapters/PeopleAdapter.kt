package tv.ridal.UI.Adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.HDRezka.Movie
import tv.ridal.UI.View.PersonView


class PeopleAdapter(private val people: ArrayList<Movie.Person>) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>()
{

    private var onPersonClick: ((Movie.Person) -> Unit)? = null
    fun onPersonClick(l: ((Movie.Person) -> Unit))
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
                    onPersonClick?.invoke( people[adapterPosition] )
                }
            }
        }

        fun bind(current: Movie.Person)
        {
            val personView = itemView as PersonView
            personView.apply {
                loadPersonPhoto(current.url!!)
                personName = current.name!!
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