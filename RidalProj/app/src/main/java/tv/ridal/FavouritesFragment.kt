package tv.ridal

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.ActionBar.BigActionBar
import tv.ridal.Application.Locale
import tv.ridal.Application.Theme
import tv.ridal.Components.Layout.LayoutHelper


class FavouritesFragment : BaseFragment()
{
    override val stableTag: String
        get() = "FavouritesFragment"

    companion object
    {
        fun instance(): FavouritesFragment
        {
            return FavouritesFragment()
        }
    }

    private lateinit var rootFrame: FrameLayout
    private lateinit var actionBar: BigActionBar
//    private lateinit var scroll: ScrollView
//    private lateinit var scrollFrame: FrameLayout
//    private lateinit var sortingCell: View
//    private lateinit var foldersView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        rootFrame = FrameLayout(requireContext()).apply {
            layoutParams = LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            )

            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        createActionBar()
        rootFrame.addView(actionBar, LayoutHelper.createFrame(
            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.TOP
        ))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return rootFrame
    }

    override fun onResume()
    {
        super.onResume()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onStop()
    {
        super.onStop()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }


    private fun createActionBar()
    {
        val menu = BigActionBar.Menu(requireContext()).apply {
            addItem(Theme.drawable(R.drawable.folder_new, Theme.color_text)) {
                showNewFolderFragment()
            }
        }

        actionBar = BigActionBar(requireContext()).apply {
            title = Locale.text(Locale.text_favourites)

            this.menu = menu
        }
    }

    private fun showNewFolderFragment() {}

}





































//