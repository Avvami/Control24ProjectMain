package com.example.control24projectmain.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.InternetConnectionCheck
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityLoginBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var backPressedOnce = false
    private lateinit var toast: StyleableToast
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                // If everything is fine, then proceed
                else -> {
                    // Get entered credentials
                    val login: String = binding.loginET.text.toString().trim {it <= ' '}
                    val password: String = binding.passwordET.text.toString().trim {it <= ' '}

                    val progressBar = binding.progressBar
                    progressBar.isIndeterminate = true

                    // Make an HTTP request to the Database using coroutines
                    coroutineScope.launch {
                        progressBar.visibility = View.VISIBLE
                        binding.loginTV.visibility = View.INVISIBLE
                        progressBar.isIndeterminate = false
                        progressBar.progress = 0

                        try {
                            val response = HttpRequestHelper.makeHttpRequest("http://91.193.225.170:8012/login2&$login&$password")
                            // Save login cred to the UserManager object
                            UserManager.saveLoginCredentials(context = this@LoginActivity, login = login, password = password)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("USERNAME", login)
                            startActivity(intent)
                            //overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                            finish()
                        } catch (e: BadRequestException) {
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
        }
    }

    class BadRequestException(message: String) : Exception(message)

    // Double back press to close the app
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
}