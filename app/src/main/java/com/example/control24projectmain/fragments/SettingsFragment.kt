package com.example.control24projectmain.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.activities.AboutAppActivity
import com.example.control24projectmain.activities.HelpActivity
import com.example.control24projectmain.activities.LoginActivity
import com.example.control24projectmain.activities.MapProviderActivity
import com.example.control24projectmain.activities.ObjectsListViewActivity
import com.example.control24projectmain.activities.ThemeActivity
import com.example.control24projectmain.databinding.FragmentSettingsBinding
import com.example.control24projectmain.themeChange

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onStart() {
        super.onStart()
        binding.darkThemeMSwitch.isChecked = UserManager.getThemeState(requireContext()) != "OFF"
        binding.themeStateTV.text = when (UserManager.getThemeState(requireContext())) {
            "OFF" -> requireContext().resources.getString(R.string.disabled)
            "ON" -> requireContext().resources.getString(R.string.enabled)
            "SCHEDULED" -> requireContext().resources.getString(R.string.scheduled, setTimeText())
            "SYSTEM" -> requireContext().resources.getString(R.string.system_default)
            else -> requireContext().resources.getString(R.string.disabled)
        }.toString()

        binding.mapUsingTV.text = when (UserManager.getSelectedMap(requireContext())) {
            "YANDEX" -> requireContext().resources.getString(R.string.yandex_maps)
            "OSM" -> requireContext().resources.getString(R.string.open_street_maps)
            else -> requireContext().resources.getString(R.string.yandex_maps)
        }.toString()

        binding.listViewTV.text = when (UserManager.getObjectsListView(requireContext())) {
            false -> requireContext().resources.getString(R.string.objects_short)
            true -> requireContext().resources.getString(R.string.objects_detailed)
        }.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        val switchToggle = binding.darkThemeMSwitch

        // Set the username in Text View
        val username = arguments?.getString("USERNAME")
        binding.usernameTemplateTV.text = username

        // Click on log out button, delete login cred and go to login activity
        binding.logoutCLButton.setOnClickListener {
            // Clear saved login session and start login screen
            UserManager.clearLoginCredentials(requireContext())
            UserManager.clearExpandedListItem(requireContext())
            UserManager.clearDisplayedItems(requireContext())
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.scale_out)
            activity?.finish()
        }

        // Go to the Theme Activity
        binding.darkThemeCL.setOnClickListener {
            val intent = Intent(activity, ThemeActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }

        // Set the dark theme for the app from function inside the main activity, click on switch toggle
        switchToggle.setOnClickListener {
            if (switchToggle.isChecked) {
                themeChange(requireContext(), "ON")
                UserManager.saveThemeState(requireContext(), "ON")
            } else {
                themeChange(requireContext(), "OFF")
                UserManager.saveThemeState(requireContext(), "OFF")
                binding.themeStateTV.text = requireContext().resources.getString(R.string.disabled)
            }
        }

        // Map provider button is clicked then open a map provider activity
        binding.mapSettingsCL.setOnClickListener {
            val intent = Intent(activity, MapProviderActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }

        // Objects list view button is clicked then open a objects list view activity
        binding.objectsListViewSettingsCL.setOnClickListener {
            val intent = Intent(activity, ObjectsListViewActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }

        // About app button is clicked then open a about app activity
        binding.aboutAppCL.setOnClickListener {
            val intent = Intent(activity, AboutAppActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }

        // Help button is clicked then open a help activity
        binding.helpCL.setOnClickListener {
            val intent = Intent(activity, HelpActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }

        return binding.root
    }

    // Set time text
    private fun setTimeText(): String {
        // Get start and end time
        val startTime = UserManager.getScheduledTime(requireContext(), "startTime")
        val endTime = UserManager.getScheduledTime(requireContext(), "endTime")

        // Set text to default otherwise set what we got
        return if (startTime != null && endTime != null) {
            "$startTime - $endTime"
        } else {
            "Not set"
        }
    }
}