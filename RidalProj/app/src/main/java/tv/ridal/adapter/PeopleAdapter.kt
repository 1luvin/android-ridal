package tv.ridal.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.hdrezka.Movie
import tv.ridal.ui.view.PersonView


class PeopleAdapter(
    private val people: ArrayList<Movie.NameUrl>
) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    private var onPersonClick: ((Drawable) -> Unit)? = null
    fun onPersonClick(l: ((Drawable) -> Unit)?) {
        onPersonClick = l
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            (itemView as PersonView).apply {
                setOnClickListener {
                    onPersonClick?.invoke(photo!!)
                }
            }
        }

        fun bind(person: Movie.NameUrl) {
            (itemView as PersonView).setPerson(person)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleAdapter.ViewHolder {
        val view = PersonView(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleAdapter.ViewHolder, position: Int) {
        holder.bind(people[position])
    }

    override fun getItemCount(): Int = people.size
}