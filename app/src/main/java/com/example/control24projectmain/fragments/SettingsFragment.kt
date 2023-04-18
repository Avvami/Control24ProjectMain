package com.example.control24projectmain.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.UserManager.clearLoginCredentials
import com.example.control24projectmain.activities.LoginActivity
import com.example.control24projectmain.activities.MainActivity
import com.example.control24projectmain.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        switchToggle.isChecked = isDarkModeOn == true

        // Set the username in Text View
        val username = arguments?.getString("USERNAME")
        binding.usernameTemplateTV.text = username

        binding.logoutCLButton.setOnClickListener {
            // Clear saved login session and start login screen
            clearLoginCredentials(requireContext())
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.darkThemeCL.setOnClickListener {
            switchToggle.isChecked = !switchToggle.isChecked
            (activity as MainActivity).isDarkThemeSet(switchToggle.isChecked)
        }

        switchToggle.setOnCheckedChangeListener { _, isChecked ->
            (activity as MainActivity).isDarkThemeSet(isChecked)
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

    /*private fun saveDarkModeState(isDarkModeOn: Boolean) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("isDarkModeOn", isDarkModeOn)
            commit()
        }
    }*/
}