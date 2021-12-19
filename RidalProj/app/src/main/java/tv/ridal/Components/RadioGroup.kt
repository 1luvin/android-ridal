package tv.ridal.Components

import android.widget.LinearLayout
import tv.ridal.ApplicationActivity
import tv.ridal.Cells.RadioCell

class RadioGroup : LinearLayout(ApplicationActivity.instance())
{
    private var radioCells: ArrayList<RadioCell> = ArrayList()

    init
    {
        orientation = LinearLayout.VERTICAL
    }

    fun addRadio(text: String)
    {
        val radioCell = RadioCell().apply {
            this.text = text
        }
        addView(radioCell)

        radioCells.add(radioCell)
    }

    fun check(text: String)
    {
        val checkedRadio: RadioCell? = radioCells.find {
            it.isChecked
        }
        checkedRadio?.setChecked(false)

        val radioCell: RadioCell? = radioCells.find {
            it.text == text
        }
        radioCell?.setChecked(true)
    }

    fun size(): Int
    {
        return radioCells.size
    }
}





































//