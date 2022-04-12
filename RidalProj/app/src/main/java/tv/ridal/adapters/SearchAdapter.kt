package tv.ridal.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import tv.ridal.App
import tv.ridal.ui.cell.SearchResultCell
import tv.ridal.hdrezka.SearchResult
import kotlin.random.Random

class SearchAdapter(private var results: ArrayList<SearchResult>) : BaseAdapter()
{
    val context: Context
        get() = App.instance().applicationContext

    override fun getCount(): Int {
        return results.size
    }

    override fun getItem(position: Int): SearchResult
    {
        return results[position]
    }

    override fun getItemId(position: Int): Long
    {
        return Random.nextLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val result: SearchResult = getItem(position)

        val resultView = SearchResultCell(context).apply {
            movieName = result.movieName
            movieData = result.movieData
            movieRating = result.movieRating
        }

        return resultView
    }

}





































//