package tv.ridal


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tunjid.androidx.navigation.MultiStackNavigator
import com.tunjid.androidx.navigation.Navigator
import com.tunjid.androidx.navigation.multiStackNavigationController
import tv.ridal.Application.UserData.User
import tv.ridal.Application.Theme
import tv.ridal.Utils.Utils
import java.io.*

class ApplicationActivity : BaseActivity()
{

    companion object {
        val tabs = intArrayOf(R.id.navigation, R.id.search)

        @Volatile
        private var INSTANCE: ApplicationActivity? = null
        fun instance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApplicationActivity().also {
                    INSTANCE = it
                }
            }

        fun currentFragment() : Fragment
        {
            return instance().multiStackNavigator.current!!
        }
    }

    val multiStackNavigator: MultiStackNavigator by multiStackNavigationController(
        tabs.size,
        R.id.content_container
    ) { index ->
        when(index)
        {
            0 -> CatalogFragment.newInstance() to CatalogFragment.TAG
            1 -> {
                val f = SearchFragment.instance()
                Pair(f, f.stableTag)
            }
//            2 -> {
//                val f = FavouritesFragment.instance()
//                Pair(f, f.stableTag)
//            }
            else -> Fragment() to "Fragment"
        }
    }

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application)

        INSTANCE = this

        Utils.checkDisplaySize(this)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.apply {
            multiStackNavigator.stackSelectedListener = { menu.findItem(tabs[it])?.isChecked = true}
            multiStackNavigator.transactionModifier = { incomingFragment ->
                val current = multiStackNavigator.current
                if (current is Navigator.TransactionModifier) current.augmentTransaction(this, incomingFragment)
                else { zoom() }
            }
            multiStackNavigator.stackTransactionModifier = { fade() }

            setOnApplyWindowInsetsListener { v, insets -> insets }
            setOnNavigationItemSelectedListener { multiStackNavigator.show(tabs.indexOf(it.itemId)).let { true } }
            setOnNavigationItemReselectedListener { multiStackNavigator.activeNavigator.clear() }
        }

        bottomNavigationView.apply {
            setBackgroundColor(Theme.color(Theme.color_bg))

            itemIconTintList = ColorStateList(
                arrayOf(
                    intArrayOf( -android.R.attr.state_checked ),
                    intArrayOf( android.R.attr.state_checked )
                ),
                intArrayOf(
                    Theme.color(Theme.color_bottomNavIcon_inactive),
                    Theme.color(Theme.color_bottomNavIcon_active),
                )
            )

            itemRippleColor = ColorStateList.valueOf(Theme.alphaColor(Theme.color(Theme.color_bottomNavIcon_active), 0.05F))
        }

        onBackPressedDispatcher.addCallback(this) { if( ! multiStackNavigator.pop()) finish() }

        createUserDataObject()
    }

    private fun createUserDataObject()
    {
        var canWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var canRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (canWrite == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions( arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1337)
        }
        if (canRead == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions( arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1338)
        }

        val userSettingsFile = File(this.filesDir, "user.settings")
        if ( userSettingsFile.exists() )
        {
            val fis = FileInputStream(userSettingsFile)
            val ois = ObjectInputStream(fis)

            User.settings = ois.readObject() as User.Settings

            ois.close()
        }
        else
        {
            println("LOLOLOLOLOLOLOLOL")
            userSettingsFile.parentFile.mkdirs()
            println(userSettingsFile.name)
            //userSettingsFile.createNewFile()

            val fos = FileOutputStream(userSettingsFile)
            val oos = ObjectOutputStream(fos)

            User.createSettings()
            oos.writeObject(User.settings)

            oos.close()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()

        updateUserSettings()
    }

    private fun updateUserSettings()
    {
        val fos = FileOutputStream("user.settings")
        val oos = ObjectOutputStream(fos)

        oos.writeObject(User.settings)

        oos.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode)
        {
            1337 ->
            {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createUserDataObject()
                }
                else
                {
                    println("You fucking suck!")
                }
            }
            1338 ->
            {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    createUserDataObject()
                }
                else
                {
                    println("You fucking suck!")
                }
            }
        }
    }


    private fun FragmentTransaction.zoom()
    {
        this.setCustomAnimations(
            R.anim.zoom_in,
            R.anim.zoom_out,
            R.anim.zoom_pop_in,
            R.anim.zoom_pop_out
        )
    }

    private fun FragmentTransaction.fade()
    {
        this.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_out,
            R.anim.fade_in
        )
    }

}

































//