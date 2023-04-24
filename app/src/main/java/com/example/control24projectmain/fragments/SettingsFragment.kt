package com.example.control24projectmain.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager.clearLoginCredentials
import com.example.control24projectmain.activities.AboutAppActivity
import com.example.control24projectmain.activities.HelpActivity
import com.example.control24projectmain.activities.LoginActivity
import com.example.control24projectmain.activities.MainActivity
import com.example.control24projectmain.activities.ObjectsListViewActivity
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
        val isDarkModeOn = arguments?.getBoolean("DARK_THEME")

        // Switch toggle state if the app is in dark or light mode
        switchToggle.isChecked = isDarkModeOn == true

        // Set the username in Text View
        val username = arguments?.getString("USERNAME")
        binding.usernameTemplateTV.text = username

        // Click on log out button, delete login cred and go to login activity
        binding.logoutCLButton.setOnClickListener {
            // Clear saved login session and start login screen
            clearLoginCredentials(requireContext())
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Set the dark theme for the app from function inside the main activity, click on whole button
        binding.darkThemeCL.setOnClickListener {
            switchToggle.isChecked = !switchToggle.isChecked
            (activity as MainActivity).isDarkThemeSet(switchToggle.isChecked)
        }

        // Set the dark theme for the app from function inside the main activity, click on switch toggle
        switchToggle.setOnCheckedChangeListener { _, isChecked ->
            (activity as MainActivity).isDarkThemeSet(isChecked)
        }

        // Map provider button is clicked then open a map provider activity
        binding.mapProviderCL.setOnClickListener {
            /*whats ap*/
        }

        // Objects list view button is clicked then open a objects list view activity
        binding.objectsListViewSettingsCL.setOnClickListener {
            val intent = Intent(activity, ObjectsListViewActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        // About app button is clicked then open a about app activity
        binding.aboutAppCL.setOnClickListener {
            val intent = Intent(activity, AboutAppActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        // Help button is clicked then open a help activity
        binding.helpCL.setOnClickListener {
            val intent = Intent(activity, HelpActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        return binding.root
    }
}