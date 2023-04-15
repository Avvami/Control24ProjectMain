package com.example.control24projectmain.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.UserManager.clearLoginCredentials
import com.example.control24projectmain.activities.LoginActivity
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
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val isDarkModeOn = sharedPref?.getBoolean("isDarkModeOn", false) ?: false

        switchToggle.isChecked = isDarkModeOn

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val username = arguments?.getString("USERNAME")
        binding.usernameTemplateTV.text = username

        /*coroutineScope.launch {
            val loginCredentials = UserManager.getLoginCredentials(requireContext())
            binding.usernameTemplateTV.text = loginCredentials?.first
        }*/

        binding.logoutCLButton.setOnClickListener {
            clearLoginCredentials(requireContext())
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
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