package com.example.control24projectmain.activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.control24projectmain.CombinedResponse
import com.example.control24projectmain.FirstResponse
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.R
import com.example.control24projectmain.SharedViewModel
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityMainBinding
import com.example.control24projectmain.fragments.ListFragment
import com.example.control24projectmain.fragments.MapFragment
import com.example.control24projectmain.fragments.SettingsFragment
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sharedViewModel by viewModels<SharedViewModel>()
    private var backPressedOnce = false
    private lateinit var toast: StyleableToast
    private var selectedItemId: Int = R.id.list_menu

    private val bundle = Bundle()
    private val listFragment = ListFragment()
    private val mapFragment = MapFragment()
    private val settingsFragment = SettingsFragment()
    private var coroutineJob: Job? = null
    private val intervalMillis = 60000L // 60 seconds in milliseconds
    private var login: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set elevation to 0 for bottomNavVie
        binding.bottomNavigationView.elevation = 0f

        // Set margin for bottomNavView when user has a virtual navigation control
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.leftMargin = insets.left
            layoutParams?.bottomMargin = insets.bottom
            layoutParams?.rightMargin = insets.right

            WindowInsetsCompat.CONSUMED
        }

        // Get user credentials
        val loginCredentials = UserManager.getLoginCredentials(this@MainActivity)
        login = loginCredentials?.first
        password = loginCredentials?.second

        // Get the username from login or splash screen
        val username = intent.getStringExtra("USERNAME")
        bundle.putString("USERNAME", username)

        // Pass the login to settings fragment
        settingsFragment.arguments = bundle

        // Restore the previously selected nav item and fragment
        if (savedInstanceState != null) {
            selectedItemId = savedInstanceState.getInt("selectedFragmentId")
            replaceFragment(selectedItemId)
            binding.bottomNavigationView.selectedItemId = selectedItemId
        } else {
            replaceFragment(selectedItemId)
            binding.bottomNavigationView.selectedItemId = selectedItemId
        }

        // Bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener {menuItem ->
            // Check if the selected item is the same as the current fragment
            if (menuItem.itemId != selectedItemId) {
                selectedItemId = menuItem.itemId
                replaceFragment(selectedItemId)
            }
            true
        }
    }

    // Replace Fragment in the frameLayout
    fun replaceFragment(menuItemId: Int) {
        binding.bottomNavigationView.selectedItemId = menuItemId
        val fragment = when (menuItemId) {
            R.id.list_menu -> listFragment
            R.id.map_menu -> mapFragment
            R.id.settings_menu -> settingsFragment
            else -> throw IllegalArgumentException("Invalid menu item id.")
        }

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

    // Start coroutine
    override fun onStart() {
        super.onStart()
        UserManager.clearResponse(this@MainActivity)
        coroutineScopeLaunch()
    }

    // Stop coroutine
    override fun onStop() {
        super.onStop()
        coroutineJob?.cancel()
    }

    // Coroutine for http requests to a database
    private fun coroutineScopeLaunch() {
        coroutineJob = lifecycleScope.launch {
            while (true) { // Repeat indefinitely
                try {
                    // Make the HTTP request and parse the response
                    val httpResponseFirst = HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/login2&$login&$password")
                    val gson = Gson()
                    val firstResponseData = gson.fromJson(httpResponseFirst, FirstResponse::class.java)
                    val httpResponseSecond = HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/update2&${firstResponseData.key}")

                    if (UserManager.getResponse(this@MainActivity) != httpResponseSecond) {
                        val json1 = JSONObject(httpResponseFirst)
                        val json2 = JSONObject(httpResponseSecond)

                        val mergedJson = JSONObject()

                        val mergedObjects = JSONArray()
                        val objects1 = json1.getJSONArray("objects")
                        val objects2 = json2.getJSONArray("objects")

                        for (i in 0 until objects1.length()) {
                            val obj1 = objects1.getJSONObject(i)
                            val obj2 = objects2.getJSONObject(i)

                            val mergedObj = JSONObject()
                            mergedObj.put("id", obj1.get("id"))
                            mergedObj.put("name", obj1.get("name"))
                            mergedObj.put("category", obj1.get("category"))
                            mergedObj.put("client", obj1.get("client"))
                            mergedObj.put("avto_no", obj1.get("avto_no"))
                            mergedObj.put("avto_model", obj1.get("avto_model"))
                            mergedObj.put("gmt", obj2.get("gmt"))
                            mergedObj.put("lat", obj2.get("lat"))
                            mergedObj.put("lon", obj2.get("lon"))
                            mergedObj.put("speed", obj2.get("speed"))
                            mergedObj.put("heading", obj2.get("heading"))
                            mergedObj.put("gps", obj2.get("gps"))
                            mergedObjects.put(mergedObj)
                        }

                        mergedJson.put("objects", mergedObjects)
                        val finalJsonStr = mergedJson.toString()

                        val combinedData = gson.fromJson(finalJsonStr, CombinedResponse::class.java)
                        // Update the bundle with the new data
                        bundle.putSerializable("OBJECTS_DATA", combinedData)
                        sharedViewModel.bundleLiveData.postValue(bundle)
                        Log.i("HDFJSDHFK", "We update data")
                        UserManager.saveResponse(this@MainActivity, httpResponseSecond)
                    } else {
                        // Do nothing
                        Log.i("HDFJSDHFK", "We don't update data")
                    }

                    // Wait for the specified interval before making the next request
                    delay(intervalMillis)
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        // Show toast message only if the exception is not a CancellationException
                        StyleableToast.makeText(
                            this@MainActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT,
                            R.style.CustomStyleableToast
                        ).show()
                    }

                    // Wait for the specified interval even if an error occurs
                    delay(intervalMillis)
                }
            }
        }
    }
}