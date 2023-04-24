package com.example.control24projectmain.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.control24projectmain.R
import com.example.control24projectmain.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if the previous activity is loginActivity
        val previousLoginActivity = intent.getBooleanExtra("fromLoginActivity", false)
        if (previousLoginActivity) {
            // Show the authorize problem text
            binding.loginProblemTV.visibility = View.VISIBLE
        }

        // Go back when click on textView
        binding.helpBackCL.setOnClickListener {
            onBackPressed()
        }

        // Open an email link
        binding.emailLinkTV.setOnClickListener {
            val email = "mailto:${binding.emailLinkTV.text}"
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse(email)
            startActivity(emailIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }

        // Open an phone link
        binding.supportNumberLinkTV.setOnClickListener {
            val phoneNumber = "tel:${binding.supportNumberLinkTV.text}"
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse(phoneNumber)
            startActivity(dialIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }

        // Open an youtube link
        binding.youtubeGuideLinkTV.setOnClickListener {
            val youtubeUrl = binding.youtubeGuideLinkTV.text.toString()
            val youtubeIntent = Intent(Intent.ACTION_VIEW)
            youtubeIntent.data = Uri.parse(youtubeUrl)
            startActivity(youtubeIntent)
            overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
        }
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}