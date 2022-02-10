package tv.ridal
//
//import android.content.Context
//import android.os.Bundle
//import android.view.*
//import android.widget.EdgeEffect
//import android.widget.FrameLayout
//import android.widget.LinearLayout
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import tv.ridal.Ui.ActionBar.ActionBar
//import tv.ridal.Ui.ActionBar.BigActionBar
//import tv.ridal.Application.Database.ApplicationDatabase
//import tv.ridal.Application.Database.Database
//import tv.ridal.Application.Locale
//import tv.ridal.Application.Theme
//import tv.ridal.Ui.Cells.CatalogSectionCell
//import tv.ridal.Ui.Cells.EmptyFolderCell
//import tv.ridal.Ui.Layout.LayoutHelper
//import tv.ridal.Ui.Popup.BottomPopup
//import tv.ridal.Ui.View.ClearableInputView
//import tv.ridal.Ui.View.LoadingTextView
//import tv.ridal.Utils.Utils
//import java.util.*
//
//
//class FavouritesFragment : BaseFragment()
//{
//    override val stableTag: String
//        get() = "FavouritesFragment"
//
//    companion object
//    {
//        @Volatile
//        private var INSTANCE: FavouritesFragment? = null
//        fun instance() =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: FavouritesFragment().also {
//                    INSTANCE = it
//                }
//            }
//    }
//
//    private lateinit var rootFrame: FrameLayout
//    private lateinit var actionBar: BigActionBar
//    private lateinit var foldersFrame: FrameLayout
//    private lateinit var loadingView: LoadingTextView
////    private lateinit var scroll: ScrollView
////    private lateinit var scrollFrame: FrameLayout
////    private lateinit var sortingCell: View
//    private lateinit var foldersView: RecyclerView
//
//
//    override fun onCreate(savedInstanceState: Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//
//        rootFrame = FrameLayout(requireContext()).apply {
//            layoutParams = LayoutHelper.createFrame(
//                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
//            )
//
//            setBackgroundColor(Theme.color(Theme.color_bg))
//        }
//
//        createActionBar()
//        rootFrame.addView(actionBar, LayoutHelper.createFrame(
//            LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
//            Gravity.START or Gravity.TOP
//        ))
//
//        actionBar.measure(0, 0)
//
//        foldersFrame = FrameLayout(requireContext()).apply {
////            setBackgroundColor(Theme.color(Theme.color_main))
//        }
//        rootFrame.addView(foldersFrame, LayoutHelper.createFrame(
//            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
//            Gravity.TOP,
//            0, Utils.px(actionBar.measuredHeight), 0, 0
//        ))
//
//        loadingView = LoadingTextView(requireContext()).apply {
//            text = "Мамочка"
//            textSize = 60F
//            typeface = Theme.typeface(Theme.tf_bold)
//
//            color = Theme.color(Theme.color_text)
//            loadColor = Theme.color(Theme.color_main)
//            loadSpanWidth = 0.6F
//            loadSpeed = 1.2F
//
//            setOnClickListener {
//                startLoading()
//            }
//        }
//
//        foldersFrame.addView(loadingView, LayoutHelper.createFrame(
//            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
//            Gravity.CENTER
//        ))
//
//        loadingView.startLoading()
//
////        lifecycleScope.launch {
////            loadingView.startLoading()
////        }
//
////        createFoldersView()
////        rootFrame.addView(foldersView, LayoutHelper.createFrame(
////            LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
////            Gravity.START or Gravity.TOP,
////            0, Utils.px(actionBar.measuredHeight), 0, 0
////        ))
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
//    {
//        return rootFrame
//    }
//
//    override fun onResume()
//    {
//        super.onResume()
//
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }
//
//    override fun onStop()
//    {
//        super.onStop()
//
//        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//    }
//
//
//    private fun createActionBar()
//    {
//        val menu = BigActionBar.Menu(requireContext()).apply {
//            addItem(Theme.drawable(R.drawable.folder_new, Theme.color_text)) {
//                showRenameFolderPopup()
//            }
//        }
//
//        actionBar = BigActionBar(requireContext()).apply {
//            title = Locale.text(Locale.text_favourites)
//
//            this.menu = menu
//        }
//    }
//
//    private fun createFoldersView()
//    {
//        foldersView = RecyclerView(requireContext()).apply {
//            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
//                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
//                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
//                }
//            }
//            clipToPadding = false
//
//            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//
//            adapter = FoldersAdapter()
//        }
//    }
//
//    private fun showNewFolderPopup()
//    {
//        val popup = BottomPopup(requireContext())
//
//        val actionBar = ActionBar(requireContext()).apply {
//            background = Theme.createRect(Theme.color_bg, radii = FloatArray(4).apply {
//                fill(Utils.dp(16F))
//            })
//
//            title = Locale.text(Locale.text_newFolder)
//
//            actionButtonIcon = Theme.drawable(R.drawable.close, Theme.color_actionBar_back)
//            onActionButtonClick {
//                popup.dismiss()
//            }
//        }
//
//        val inputView = ClearableInputView(requireContext()).apply {
//            editText.apply {
//                hint = Locale.text(Locale.text_folderName)
//                setHintTextColor(Theme.color(Theme.color_text2))
//            }
//        }
//
//        val contentView = LinearLayout(requireContext()).apply {
//            orientation = LinearLayout.VERTICAL
//
//            background = Theme.createRect(Theme.color_bg, radii = floatArrayOf(
//                Utils.dp(12F), Utils.dp(12F), 0F, 0F
//            ))
//
//            addView(actionBar)
//            addView(inputView)
//        }
//
//        popup.apply {
//            setContentView(contentView)
//        }
//
//        popup.show()
//    }
//
//    private fun showRenameFolderPopup()
//    {
//        loadingView.stopLoading()
//
//        val popup = BottomPopup(requireContext())
//
//        val actionBar = ActionBar(requireContext()).apply {
//            background = Theme.createRect(Theme.color_bg, radii = FloatArray(4).apply {
//                fill(Utils.dp(16F))
//            })
//
//            title = Locale.text(Locale.text_renameTo)
//
//            actionButtonIcon = Theme.drawable(R.drawable.close, Theme.color_actionBar_back)
//            onActionButtonClick {
//                popup.dismiss()
//            }
//        }
//
//        val inputView = ClearableInputView(requireContext()).apply {
//            editText.apply {
//                hint = Locale.text(Locale.text_newFolderName)
//                setHintTextColor(Theme.color(Theme.color_text2))
//            }
//        }
//
//        val contentView = LinearLayout(requireContext()).apply {
//            orientation = LinearLayout.VERTICAL
//
//            background = Theme.createRect(Theme.color_bg, radii = floatArrayOf(
//                Utils.dp(12F), Utils.dp(12F), 0F, 0F
//            ))
//
//            addView(actionBar)
//            addView(inputView)
//        }
//
//        popup.apply {
//            setContentView(contentView)
//        }
//
//        popup.show()
//    }
//
//
//    private fun showNewFolderFragment()
//    {
//        lifecycleScope.launch {
//            var count = 0
//            withContext(Dispatchers.IO)
//            {
//                val folderDao = ApplicationDatabase.instance().folderDao()
//                folderDao.apply {
//                    insert(
//                        Database.Folder("Shit${System.currentTimeMillis()}", 5, Date(), Date())
//                    )
//                }
//                count = folderDao.count()
//            }
//            println(count)
//            foldersView.adapter?.notifyDataSetChanged()
//        }
//    }
//
//
//
//    class FolderView(context: Context) : LinearLayout(context)
//    {
//        var type: Type = Type.EMPTY
//            set(value) {
//                if (value == type) return
//                field = value
//
//                this.removeView(folderHeaderCell)
//
//                if (type == Type.NOT_EMPTY)
//                {
//                    folderHeaderCell = CatalogSectionCell(context).apply {
//                        sectionName = folderName
//                        sectionSubtext = folderSize.toString()
//                    }
//                }
//                else if (type == Type.EMPTY)
//                {
//                    folderHeaderCell = EmptyFolderCell(context).apply {
//                        this.folderName = folderName
//                    }
//                }
//
//                this.addView(folderHeaderCell, 0)
//            }
//
//        private var folderHeaderCell: View? = null
//
//        var folderName: String = ""
//            set(value) {
//                field = value
//
//                if (folderHeaderCell == null) return
//
//                if (type == Type.NOT_EMPTY) {
//                    (folderHeaderCell as CatalogSectionCell).sectionName = folderName
//                } else if (type == Type.EMPTY) {
//                    (folderHeaderCell as EmptyFolderCell).folderName = folderName
//                }
//            }
//
//        var folderSize: Int = 0
//            set(value) {
//                field = value
//
//                if (folderHeaderCell == null) return
//
//                if (type == Type.NOT_EMPTY) {
//                    (folderHeaderCell as CatalogSectionCell).sectionSubtext = folderSize.toString()
//                }
//            }
//
//        init
//        {
//            orientation = LinearLayout.VERTICAL
//        }
//
//        enum class Type
//        {
//            NOT_EMPTY, EMPTY
//        }
//    }
//
//    class FoldersAdapter : RecyclerView.Adapter<FoldersAdapter.ViewHolder>()
//    {
//        private lateinit var folderDao: Database.FolderDao
//        private lateinit var folders: List<Database.Folder>
//
//        private suspend fun initFolders() = withContext(Dispatchers.IO) {
//            folderDao = ApplicationDatabase.instance().folderDao()
//            folders = folderDao.allFolders()
//            println(folderDao.count())
//        }
//
//        init
//        {
//            FavouritesFragment.instance().lifecycleScope.launch {
//                initFolders()
//                println("SHIT")
//                notifyDataSetChanged()
//            }
//        }
//
//        inner class ViewHolder(folderView: FolderView) : RecyclerView.ViewHolder(folderView)
//        {
//
//            init
//            {
//
//            }
//
//            fun bind(current: Database.Folder)
//            {
//                val folder = itemView as FolderView
//                folder.apply {
//                    type = if (current.isEmpty()) {
//                        FolderView.Type.EMPTY
//                    } else FolderView.Type.NOT_EMPTY
//
//                    folderName = current.folderName
//                    folderSize = current.size
//                }
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
//        {
//            val folder = FolderView(parent.context)
//
//            return ViewHolder(folder)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int)
//        {
//            if ( ! this::folders.isInitialized) return
//
//            holder.bind(folders[position])
//        }
//
//        override fun getItemCount(): Int
//        {
//            return if ( ! this::folders.isInitialized) {
//                0
//            } else {
//                folders.size
//            }
//        }
//
//    }
//
//}





































//