package tv.ridal.Components.Layout

import android.content.Context
import android.widget.LinearLayout
import tv.ridal.Components.Cells.CheckCell

class SingleCheckGroup(context: Context) : LinearLayout(context)
{
    private var checkCells: ArrayList<CheckCell> = ArrayList()

    init
    {
        orientation = VERTICAL
    }

    fun addCheck(text: String)
    {
        val checkCell = CheckCell(context).apply {
            this.text = text

            setOnClickListener {
                if (this.isChecked) return@setOnClickListener

                check(this.text)
            }
        }
        addView(checkCell)

        checkCells.add(checkCell)
    }

    fun check(text: String)
    {
        val checkedCell = checkCells.find {
            it.isChecked
        }
        checkedCell?.setChecked(false)

        val toCheck = checkCells.find {
            it.text == text
        }
        toCheck?.setChecked(true)
    }

    fun currentChecked(): String = checkCells.find {
        it.isChecked
    }!!.text

    fun size(): Int = checkCells.size
}





































//