package com.example.control24projectmain.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.control24projectmain.databinding.BottomOverlayLayersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomOverlayInfo(): BottomSheetDialogFragment() {

    private lateinit var binding: BottomOverlayLayersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomOverlayLayersBinding.inflate(layoutInflater)
        return binding.root
    }
}