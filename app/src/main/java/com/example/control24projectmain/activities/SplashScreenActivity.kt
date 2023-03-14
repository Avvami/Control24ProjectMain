package com.example.control24projectmain.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.*
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val progressBar = binding.progressBar2
        val loginCredentials = UserManager.getLoginCredentials(this@SplashScreenActivity)
        if (loginCredentials != null) {
            val (login, password) = loginCredentials

            coroutineScope.launch {
                /*progressBar.isIndeterminate = false
                progressBar.progress = 0*/

                try {
                    HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/login2&$login&$password")
                    val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                    intent.putExtra("USERNAME", login)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                } catch (e: BadRequestException) {
                    // Handle the 400 error
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    // Handle other errors
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    startActivity(intent)
                    finish()
                } finally {
                    //progressBar.progress = 0
                }
            }
        } else {
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    class BadRequestException(message: String) : Exception(message)
}