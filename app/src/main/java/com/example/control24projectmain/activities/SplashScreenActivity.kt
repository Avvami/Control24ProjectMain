package com.example.control24projectmain.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDelegate
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.InternetConnectionCheck
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

        // Load loginCred if it existing
        val loginCredentials = UserManager.getLoginCredentials(this@SplashScreenActivity)

        // Internet connection check
        if (InternetConnectionCheck.isInternetConnected(this@SplashScreenActivity)) {
            // Check if it has saved credentials
            if (loginCredentials != null) {
                performLogin(
                    loginCredentials,
                    {
                        // On success
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        intent.putExtra("USERNAME", loginCredentials.first)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    },
                    {
                        // On error
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        startActivity(intent)
                        finish()
                    }
                )
            } else {
                // Get to login activity if have no credentials saved
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        } else {
            // Show dialog window on splash screen if not connected to the net
            val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_view, null)
            val dialog = Dialog(this@SplashScreenActivity)
            dialog.setContentView(dialogBinding)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.show()

            val retryBtn = dialogBinding.findViewById<RelativeLayout>(R.id.retryBtn)
            retryBtn.setOnClickListener {
                // Login on retry button if have saved cred and have connection, or goes to login screen if have no cred
                if (InternetConnectionCheck.isInternetConnected(this@SplashScreenActivity)) {
                    // Have connection, then check saved login cred
                    dialog.dismiss()
                    if (loginCredentials != null) {
                        performLogin(
                            loginCredentials,
                            {
                                // On success
                                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                                intent.putExtra("USERNAME", loginCredentials.first)
                                startActivity(intent)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                finish()
                            },
                            {
                                // On error
                                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                startActivity(intent)
                                finish()
                            }
                        )
                    } else {
                        // No saved cred -> login screen
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                } else {
                    // Click on retry button constantly show dialog window
                    dialog.dismiss()
                    dialog.show()
                }
            }

            val continueBtn = dialogBinding.findViewById<RelativeLayout>(R.id.continueBtn)
            continueBtn.setOnClickListener {
                // Almost same logic as for retry button
                if (InternetConnectionCheck.isInternetConnected(this@SplashScreenActivity)) {
                    dialog.dismiss()
                    if (loginCredentials != null) {
                        performLogin(
                            loginCredentials,
                            {
                                // On success
                                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                                intent.putExtra("USERNAME", loginCredentials.first)
                                startActivity(intent)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                finish()
                            },
                            {
                                // On error
                                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                                startActivity(intent)
                                finish()
                            }
                        )
                    } else {
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                } else {
                    dialog.dismiss()
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            }
        }
    }

    class BadRequestException(message: String) : Exception(message)

    // Function for login and catch errors
    private fun performLogin(loginCredentials: Pair<String, String>, onSuccess: () -> Unit, onError: () -> Unit) {
        val (login, password) = loginCredentials

        coroutineScope.launch {

            try {
                HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/login2&$login&$password")
                onSuccess()
            } catch (e: BadRequestException) {
                // Handle the 400 error
                onError()
            } catch (e: Exception) {
                // Handle other errors
                onError()
            }
        }
    }
}