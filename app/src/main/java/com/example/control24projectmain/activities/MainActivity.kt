package com.example.control24projectmain.activities

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.control24projectmain.R
import com.example.control24projectmain.databinding.ActivityMainBinding
import com.example.control24projectmain.fragments.ListFragment
import com.example.control24projectmain.fragments.MapFragment
import com.example.control24projectmain.fragments.ReportsFragment
import com.example.control24projectmain.fragments.SettingsFragment
import io.github.muddz.styleabletoast.StyleableToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedOnce = false
    private lateinit var toast: StyleableToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(ListFragment())
        binding.bottomNavigationView.selectedItemId = R.id.list_menu

        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.reports_menu -> replaceFragment(ReportsFragment())
                R.id.list_menu -> replaceFragment(ListFragment())
                R.id.map_menu -> replaceFragment(MapFragment())
                R.id.settings_menu -> replaceFragment(SettingsFragment())

                else -> {
                    /*Something should be here*/
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        //fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

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
}