package com.example.control24projectmain.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.control24projectmain.R
import com.example.control24projectmain.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        val switchToggle = binding.darkThemeMSwitch
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref?.getBoolean("isDarkModeOn", false) ?: false

        switchToggle.isChecked = isDarkModeOn

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.logoutCLButton.setOnClickListener {
            /*hey*/
        }

        binding.darkThemeCL.setOnClickListener {
            switchToggle.isChecked = !switchToggle.isChecked
            /*if (switchToggle.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveDarkModeState(true)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveDarkModeState(false)
            }*/
        }

        binding.mapProviderCL.setOnClickListener {
            /*whats ap*/
        }

        binding.aboutAppCL.setOnClickListener {
            /*ayo*/
        }

        binding.helpCL.setOnClickListener {
            /*chill*/
        }

        return binding.root
    }

    private fun saveDarkModeState(isDarkModeOn: Boolean) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("isDarkModeOn", isDarkModeOn)
            commit()
        }
    }
}