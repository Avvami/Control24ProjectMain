package com.example.control24projectmain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        // Get the saved data (short is default)
        isDetailedView = UserManager.getObjectsListView(this@ObjectsListViewActivity)
        val shortCheckBox = binding.shortMCheckBox
        val detailedCheckBox = binding.detailedMCheckBox

        // Change the font for either of view selected
        if (!isDetailedView) {
            shortCheckBox.isChecked = true
        } else {
            detailedCheckBox.isChecked = true
        }

        // Go back when click on textView
        binding.objectsListViewBackCL.setOnClickListener {
            onBackPressed()
        }

        // Change the state of checkBox its font and save selected
        shortCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                detailedCheckBox.isChecked = false
                UserManager.saveObjectsListView(this@ObjectsListViewActivity, false)
            }
        }

        // Change the state of checkBox its font and save selected
        detailedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                shortCheckBox.isChecked = false
                UserManager.saveObjectsListView(this@ObjectsListViewActivity, true)
            }
        }
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }
}