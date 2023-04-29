package com.example.control24projectmain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.control24projectmain.R
import com.example.control24projectmain.databinding.ActivityMapProviderBinding

class MapProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Go back when click on textView
        binding.mapProviderBackCL.setOnClickListener {
            onBackPressed()
        }
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }
}