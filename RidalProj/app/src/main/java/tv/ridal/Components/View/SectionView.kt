package tv.ridal.Components.View

import android.content.Context
import android.widget.LinearLayout
import tv.ridal.Adapters.MoviesAdapter
import tv.ridal.Cells.CatalogSectionCell

class SectionView(context: Context) : LinearLayout(context)
{

    private var nameCell: CatalogSectionCell

    var sectionName: String = ""
        set(value) {
            field = value

            nameCell.sectionName = sectionName
        }
    var sectionSubtext: String = ""
        set(value) {
            field = value

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

        nameCell = CatalogSectionCell(context)
        addView(nameCell)

        recyclerView = MoviesRecyclerView(context)
        addView(recyclerView)
    }

}





































//