package com.example.control24projectmain.activities

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.HttpResponseFirstData
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityMainBinding
import com.example.control24projectmain.fragments.ListFragment
import com.example.control24projectmain.fragments.MapFragment
import com.example.control24projectmain.fragments.SettingsFragment
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var backPressedOnce = false
    private lateinit var toast: StyleableToast
    private var selectedItemId: Int = R.id.list_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = Bundle()

        /*coroutineScope.launch {
            try {
                val loginCredentials = UserManager.getLoginCredentials(this@MainActivity)
                val login = loginCredentials?.first
                val password = loginCredentials?.second

                val rawHttpResponse = HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/login2&$login&$password")

                // Parse Http Response
                val gson = Gson()
                val parsedResponse = gson.fromJson(rawHttpResponse, HttpResponseFirstData::class.java)

                bundle.putString("KEY", parsedResponse.key)
                bundle.putParcelableArrayList("OBJECTS", parsedResponse.objects)

            } catch (e: Exception) {
                // Handle error
                StyleableToast.makeText(
                    this@MainActivity,
                    e.toString(),
                    Toast.LENGTH_SHORT,
                    R.style.CustomStyleableToast
                ).show()
            }
        }*/

        // Pass the key and objects to list fragment
        val listFragment = ListFragment()
        listFragment.arguments = bundle

        val isDarkTheme = UserManager.getThemeState(this@MainActivity)

        // Get the username from login or splash screen
        val username = intent.getStringExtra("USERNAME")
        bundle.putBoolean("DARK_THEME", isDarkTheme)
        bundle.putString("USERNAME", username)

        // Pass the login to settings fragment
        val settingsFragment = SettingsFragment()
        settingsFragment.arguments = bundle

        // Restore the previously selected nav item and fragment
        if (savedInstanceState != null) {
            selectedItemId = savedInstanceState.getInt("selectedFragmentId")
            when (selectedItemId) {
                R.id.list_menu -> replaceFragment(listFragment)
                R.id.map_menu -> replaceFragment(MapFragment())
                R.id.settings_menu -> replaceFragment(settingsFragment)
            }
            binding.bottomNavigationView.selectedItemId = selectedItemId
        } else {
            replaceFragment(listFragment)
            binding.bottomNavigationView.selectedItemId = selectedItemId
        }

        // Bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener {menuItem ->

            // Check if the selected item is the same as the current fragment
            if (menuItem.itemId == selectedItemId) {
                return@setOnItemSelectedListener true
            }

            // Switch fragment inside the frameLayout
            when (menuItem.itemId) {
                R.id.list_menu -> {
                    selectedItemId = R.id.list_menu
                    replaceFragment(listFragment)
                }
                R.id.map_menu -> {
                    selectedItemId = R.id.map_menu
                    replaceFragment(MapFragment())
                    //setStatusBarColor(R.color.black_10p)
                }
                R.id.settings_menu -> {
                    selectedItemId = R.id.settings_menu
                    replaceFragment(settingsFragment)
                }
                else -> {
                    /*Something should be here*/
                }
            }
            true
        }
    }

    // Replace Fragment in the frameLayout
    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    // Save selected Fragment when rotating or changing theme
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedFragmentId", selectedItemId)
    }

    // Double back press to close the app
    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed()
            return
        }

        backPressedOnce = true
        toast = StyleableToast.makeText(
            this,
            "Нажмите еще раз, чтобы выйти",
            Toast.LENGTH_SHORT,
            R.style.CustomStyleableToast
        )
        toast.show()

        Handler().postDelayed({
            backPressedOnce = false
            if (::toast.isInitialized) {
                toast.cancel()
            }
        }, 2000)
    }

    //Dark theme set
    fun isDarkThemeSet (darkTheme: Boolean) {
        UserManager.saveThemeState(this@MainActivity, darkTheme)
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}