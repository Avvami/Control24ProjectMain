package com.example.control24projectmain.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.InternetConnectionCheck
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityLoginBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var backPressedOnce = false
    private lateinit var toast: StyleableToast
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Do login on a login button click
        binding.loginButton.setOnClickListener {
            when {
                // Login edit view is not empty check, if it is empty then show toast + focus on edit view
                TextUtils.isEmpty(binding.loginET.text.toString().trim{ it <= ' ' }) -> {
                    StyleableToast.makeText(
                        this,
                        "Введите логин",
                        Toast.LENGTH_SHORT,
                        R.style.CustomStyleableToast
                    ).show()
                    binding.loginET.requestFocus()
                }

                // Password edit view is not empty check, if it is empty then show toast + focus on edit view
                TextUtils.isEmpty(binding.passwordET.text.toString().trim { it <= ' ' }) -> {
                    StyleableToast.makeText(
                        this,
                        "Введите пароль",
                        Toast.LENGTH_SHORT,
                        R.style.CustomStyleableToast
                    ).show()
                    binding.passwordET.requestFocus()
                }
                // If everything is fine then proceed
                else -> {
                    // Get entered credentials
                    val login: String = binding.loginET.text.toString().trim {it <= ' '}
                    val password: String = binding.passwordET.text.toString().trim {it <= ' '}

                    val progressBar = binding.progressBar
                    progressBar.isIndeterminate = true

                    // Check if user has an internet connection or not
                    if (InternetConnectionCheck.isInternetConnected(this@LoginActivity)) {
                        // If connected to the network then do login
                        performLogin(progressBar, login, password)
                    } else {
                        // Show dialog window if not connected to the network
                        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog_view, null)
                        val dialog = Dialog(this@LoginActivity)
                        dialog.setContentView(dialogBinding)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setCancelable(true)
                        dialog.show()

                        // Dialog retry button click
                        val retryBtn = dialogBinding.findViewById<RelativeLayout>(R.id.retryBtn)
                        retryBtn.setOnClickListener {
                            if (InternetConnectionCheck.isInternetConnected(this@LoginActivity)) {
                                // Dismiss the dialog and perform login
                                dialog.dismiss()
                                performLogin(progressBar, login, password)
                            } else {
                                // If no internet connection then show dialog again
                                dialog.dismiss()
                                dialog.show()
                            }
                        }

                        // Dialog continue button click just dismiss the dialog but do not perform login
                        val continueBtn = dialogBinding.findViewById<RelativeLayout>(R.id.continueBtn)
                        continueBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        // Open a help activity on a help button
        binding.loginHelpMButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, HelpActivity::class.java)
            intent.putExtra("fromLoginActivity", true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }
    }

    // Double back press to close the app
    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed()
            return
        }

        backPressedOnce = true
        toast = StyleableToast.makeText(
            this,
            "Нажмите еще раз, чтобы выйти",
            Toast.LENGTH_SHORT,
            R.style.CustomStyleableToast
        )
        toast.show()

        Handler().postDelayed({
            backPressedOnce = false
            if (::toast.isInitialized) {
                toast.cancel()
            }
        }, 2000)
    }

    // Make an HTTP request to the Database using coroutines
    private fun performLogin (progressBar: ProgressBar, login: String, password: String) {
        coroutineScope.launch {
            progressBar.visibility = View.VISIBLE
            binding.loginTV.visibility = View.INVISIBLE
            progressBar.isIndeterminate = false
            progressBar.progress = 0

            try {
                // Save login cred to the UserManager object
                HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/login2&$login&$password")
                UserManager.saveLoginCredentials(context = this@LoginActivity, login = login, password = password)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("USERNAME", login)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
                finish()
            } catch (e: HttpRequestHelper.BadRequestException) {
                // Handle the 400 error
                StyleableToast.makeText(
                    this@LoginActivity,
                    "Ошибка: Неверный логин или пароль",
                    Toast.LENGTH_SHORT,
                    R.style.CustomStyleableToast
                ).show()
            } catch (e: Exception) {
                // Handle other errors
                StyleableToast.makeText(
                    this@LoginActivity,
                    e.toString(),
                    Toast.LENGTH_SHORT,
                    R.style.CustomStyleableToast
                ).show()
            } finally {
                // Hide the progressBar
                progressBar.visibility = View.INVISIBLE
                binding.loginTV.visibility = View.VISIBLE
                progressBar.progress = 0
            }
        }
    }
}