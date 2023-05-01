package com.example.control24projectmain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityMapProviderBinding

class MapProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapProvider = UserManager.getSelectedMap(this@MapProviderActivity)
        val yandexCheckBox = binding.yandexMapsMCheckBox
        val osmCheckBox = binding.osmMCheckBox

        if (mapProvider == "YANDEX") {
            yandexCheckBox.isChecked = true
        } else {
            osmCheckBox.isChecked = true
        }

        // Save selected map
        binding.yandexMapsCL.setOnClickListener {
            if (osmCheckBox.isChecked) {
                osmCheckBox.isChecked = false
                yandexCheckBox.isChecked = true
                UserManager.saveSelectedMap(this@MapProviderActivity, "YANDEX")
            }
        }

        // Save selected map
        binding.osmCL.setOnClickListener {
            if (yandexCheckBox.isChecked) {
                yandexCheckBox.isChecked = false
                osmCheckBox.isChecked = true
                UserManager.saveSelectedMap(this@MapProviderActivity, "OSM")
            }
        }

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