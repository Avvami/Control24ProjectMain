package com.example.control24projectmain.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.control24projectmain.R
import com.example.control24projectmain.databinding.ActivityAboutAppBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AboutAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the version of the app and the last update time
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        val lastUpdateTime = packageInfo.lastUpdateTime

        // Convert timestamps to date strings
        val dateFormat = SimpleDateFormat("d MMMM yyyy 'г.'", Locale("ru"))
        val lastUpdateTimeStr = dateFormat.format(Date(lastUpdateTime))

        // Set the app version and the date
        val appVersion = resources.getString(R.string.app_version, versionName.toString(), lastUpdateTimeStr.toString())
        binding.appVersionTV.text = appVersion

        // Go back when click on a textView
        binding.aboutAppBackCL.setOnClickListener {
            onBackPressed()
        }

        // Open yandex conditions link
        binding.yandexConditionsLinkTV.setOnClickListener {
            val websiteUrl = binding.yandexConditionsLinkTV.text.toString()
            val websiteIntent = Intent(Intent.ACTION_VIEW)
            websiteIntent.data = Uri.parse(websiteUrl)
            startActivity(websiteIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }

        // Open an email link
        binding.devEmailLinkTV.setOnClickListener {
            val email = "mailto:${binding.devEmailLinkTV.text}"
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(email)
            startActivity(emailIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }

        // Open an phone link
        binding.devOfficeLinkTV.setOnClickListener {
            val phoneNumber = "tel:${binding.devOfficeLinkTV.text}"
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse(phoneNumber)
            startActivity(dialIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }

        // Open an website link
        binding.devWebsiteLinkTV.setOnClickListener {
            val websiteUrl = binding.devWebsiteLinkTV.text.toString()
            val websiteIntent = Intent(Intent.ACTION_VIEW)
            websiteIntent.data = Uri.parse(websiteUrl)
            startActivity(websiteIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }
}