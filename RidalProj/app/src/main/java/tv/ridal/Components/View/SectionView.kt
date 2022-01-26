package tv.ridal.Components.View

import android.content.Context
import android.widget.LinearLayout
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Components.Cells.CatalogSectionCell

class SectionView(context: Context) : LinearLayout(context)
{

    interface OpenListener
    {
        fun onOpen()
    }
    var openListener: OpenListener? = null

    private var nameCell: CatalogSectionCell

    var sectionName: String = ""
        set(value) {
            field = value

            nameCell.sectionName = sectionName
        }
    var sectionSubtext: String = ""
        set(value) {

            field = "$value+"

            nameCell.sectionSubtext = sectionSubtext
        }

    private var recyclerView: MoviesRecyclerView

    var adapter: MoviesAdapter? = null
        set(value) {
            field = value

            recyclerView.adapter = adapter
        }

    init
    {
        orientation = LinearLayout.VERTICAL

        nameCell = CatalogSectionCell(context).apply {
            setOnClickListener {
                openListener?.onOpen()
            }
        }
        addView(nameCell)

        recyclerView = MoviesRecyclerView(context)
        addView(recyclerView)
    }

}





































//