package com.example.control24projectmain

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val bundleLiveData = MutableLiveData<Bundle>()
}