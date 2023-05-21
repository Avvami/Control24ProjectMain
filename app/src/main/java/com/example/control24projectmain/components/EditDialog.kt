package com.example.control24projectmain.components

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.EditDialogViewBinding

interface OnDialogCloseListener {
    fun onDialogClose()
}

class EditDialog(private val itemId: Int, private val listener: OnDialogCloseListener): DialogFragment() {

    private lateinit var binding: EditDialogViewBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = EditDialogViewBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val driverInfo = UserManager.getDriverInfo(requireContext(), itemId.toString())
        binding.driverNameET.setText(if (driverInfo.first != "null") {
            driverInfo.first
        } else {
            ""
        })

        binding.driverPhoneET.setText(if (driverInfo.second != "null") {
            driverInfo.second
        } else {
            ""
        })

        binding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        binding.saveBtn.setOnClickListener {
            val driverNameET = binding.driverNameET.text.toString().ifEmpty { null }
            val driverPhoneET = binding.driverPhoneET.text.toString().ifEmpty { null }
            UserManager.saveDriverInfo(requireContext(), itemId.toString(), driverNameET, driverPhoneET)
            listener.onDialogClose()
            dialog.dismiss()
        }

        return dialog
    }
}