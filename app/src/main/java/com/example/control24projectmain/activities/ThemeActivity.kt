package com.example.control24projectmain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityThemeBinding
import com.example.control24projectmain.themeChange
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val START_TIME = "startTime"
private const val END_TIME = "endTime"

class ThemeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemeBinding
    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set scheduled time
        binding.scheduledTimeTV.text = setTimeText()

        val darkThemeState = UserManager.getThemeState(this@ThemeActivity)
        val disabledCB = binding.disabledMCheckBox
        val enabledCB = binding.enabledMCheckBox
        val scheduledCB = binding.scheduledMCheckBox
        val systemDefaultCB = binding.systemDefaultMCheckBox

        var checkedCheckBoxID = when (darkThemeState) {
            "OFF" -> binding.disabledMCheckBox
            "ON" -> binding.enabledMCheckBox
            "SCHEDULED" -> binding.scheduledMCheckBox
            "SYSTEM" -> binding.systemDefaultMCheckBox
            else -> {binding.disabledMCheckBox}
        }
        checkedCheckBoxID.isChecked = true

        // Disable dark theme
        binding.disabledCL.setOnClickListener {
            if (checkedCheckBoxID != disabledCB) {
                disabledCB.isChecked = true
                checkedCheckBoxID.isChecked = false
                checkedCheckBoxID = disabledCB
                themeChange(this@ThemeActivity, "OFF")
                UserManager.saveThemeState(this@ThemeActivity, "OFF")
            }
        }

        // Enable dark theme
        binding.enabledCL.setOnClickListener {
            if (checkedCheckBoxID != enabledCB) {
                enabledCB.isChecked = true
                checkedCheckBoxID.isChecked = false
                checkedCheckBoxID = enabledCB
                themeChange(this@ThemeActivity, "ON")
                UserManager.saveThemeState(this@ThemeActivity, "ON")
            }
        }

        // Enable scheduled dark theme
        binding.scheduledCL.setOnClickListener {
            openStartTimePicker()
            if (checkedCheckBoxID != scheduledCB) {
                scheduledCB.isChecked = true
                checkedCheckBoxID.isChecked = false
                checkedCheckBoxID = scheduledCB
            }
        }

        // Enable system default theme
        binding.systemDefaultCL.setOnClickListener {
            if (checkedCheckBoxID != systemDefaultCB) {
                systemDefaultCB.isChecked = true
                checkedCheckBoxID.isChecked = false
                checkedCheckBoxID = systemDefaultCB
                themeChange(this@ThemeActivity, "SYSTEM")
                UserManager.saveThemeState(this@ThemeActivity, "SYSTEM")
            }
        }

        // Go back when click on textView
        binding.themeBackCL.setOnClickListener {
            onBackPressed()
        }

    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        bundle.putString("THEME_STATE", UserManager.getThemeState(this@ThemeActivity))
        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }

    // Open start time picker
    private fun openStartTimePicker() {
        // Check time format
        val isSystem24Hour = DateFormat.is24HourFormat(this@ThemeActivity)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance()
        val format = if (isSystem24Hour) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }

        format.timeZone = TimeZone.getDefault()
        val date = UserManager.getScheduledTime(this, START_TIME)
            ?.let { format.parse(it) } ?: throw ParseException("Unable to parse time", 0)
        calendar.time = date

        val startPicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(calendar.get(Calendar.HOUR))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTheme(R.style.CustomMaterialTimePickerStyle)
            .setTitleText("Начало")
            .build()
        startPicker.show(supportFragmentManager, "START_TAG")

        startPicker.addOnPositiveButtonClickListener {
            // Handle the selected start time
            val startHour = startPicker.hour
            val startMinute = startPicker.minute

            // Save start time
            UserManager.saveScheduledTime(this@ThemeActivity, START_TIME, startHour, startMinute)
            binding.scheduledTimeTV.text = setTimeText()
            openEndTimePicker()
        }

        startPicker.addOnNegativeButtonClickListener {
            UserManager.saveThemeState(this@ThemeActivity, "SCHEDULED")
            themeChange(this@ThemeActivity, "SCHEDULED")
        }

        startPicker.addOnCancelListener {
            UserManager.saveThemeState(this@ThemeActivity, "SCHEDULED")
            themeChange(this@ThemeActivity, "SCHEDULED")
        }
    }

    // Open end time picker
    private fun openEndTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(this@ThemeActivity)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance()
        val format = if (isSystem24Hour) {
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }

        format.timeZone = TimeZone.getDefault()
        val date = UserManager.getScheduledTime(this, END_TIME)
            ?.let { format.parse(it) } ?: throw ParseException("Unable to parse time", 0)
        calendar.time = date

        val endPicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(calendar.get(Calendar.HOUR))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTheme(R.style.CustomMaterialTimePickerStyle)
            .setTitleText("Конец")
            .build()
        endPicker.show(supportFragmentManager, "END_TAG")

        endPicker.addOnPositiveButtonClickListener {
            // Handle the selected end time
            val endHour = endPicker.hour
            val endMinute = endPicker.minute

            // Save end time
            UserManager.saveScheduledTime(this@ThemeActivity, END_TIME, endHour, endMinute)
            binding.scheduledTimeTV.text = setTimeText()

            UserManager.saveThemeState(this@ThemeActivity, "SCHEDULED")
            themeChange(this@ThemeActivity, "SCHEDULED")
        }

        endPicker.addOnNegativeButtonClickListener {
            UserManager.saveThemeState(this@ThemeActivity, "SCHEDULED")
            themeChange(this@ThemeActivity, "SCHEDULED")
        }

        endPicker.addOnCancelListener {
            UserManager.saveThemeState(this@ThemeActivity, "SCHEDULED")
            themeChange(this@ThemeActivity, "SCHEDULED")
        }
    }

    // Set time text
    private fun setTimeText(): String {
        // Get start and end time
        val startTime = UserManager.getScheduledTime(this@ThemeActivity, START_TIME)
        val endTime = UserManager.getScheduledTime(this@ThemeActivity, END_TIME)

        // Set text to default otherwise set what we got
        return if (startTime != null && endTime != null) {
            "$startTime - $endTime"
        } else {
            "Not set"
        }
    }
}