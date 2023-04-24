package com.example.control24projectmain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityObjectsListViewBinding

class ObjectsListViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityObjectsListViewBinding
    private var isDetailedView: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectsListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if its detailed or short view selected (Short view is default)
        isDetailedView = UserManager.getObjectsListView(this@ObjectsListViewActivity)
        val shortCheckBox = binding.shortMCheckBox
        val detailedCheckBox = binding.detailedMCheckBox
        if (!isDetailedView) {
            shortCheckBox.isChecked = true
            shortCheckBox.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        } else {
            detailedCheckBox.isChecked = true
            detailedCheckBox.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        }

        // Go back when click on textView
        binding.objectsListViewBackCL.setOnClickListener {
            onBackPressed()
        }

        // Change the state of checkBox and save selected
        shortCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                detailedCheckBox.isChecked = false
                detailedCheckBox.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                shortCheckBox.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                UserManager.saveObjectsListView(this@ObjectsListViewActivity, false)
            }
        }

        // Change the state of checkBox and save selected
        detailedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                shortCheckBox.isChecked = false
                shortCheckBox.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                binding.detailedMCheckBox.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                UserManager.saveObjectsListView(this@ObjectsListViewActivity, true)
            }
        }
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}