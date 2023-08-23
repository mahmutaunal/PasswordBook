package com.mahmutalperenunal.passwordbook.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.mahmutalperenunal.passwordbook.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.Observer
import com.mahmutalperenunal.passwordbook.database.PasswordManagerDatabase
import com.mahmutalperenunal.passwordbook.repository.PasswordManagerRepository
import com.mahmutalperenunal.passwordbook.ui.factories.CreateEditViewPasswordViewModelProviderFactory
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set screen orientation to portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val database = PasswordManagerDatabase(this)
        val repository = PasswordManagerRepository(database)
        val factory = CreateEditViewPasswordViewModelProviderFactory(repository)
        val viewModel =
            ViewModelProvider(this, factory)[CreateEditViewPasswordViewModel::class.java]

        viewModel.getLockPassword().observe(this, Observer {
            if (it.isEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(1000)
                    /*val intent = Intent(this@SplashActivity, LockActivity::class.java)
                    intent.putExtra("command", "createpassword")
                    startActivity(intent)
                    finish()*/
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    /*val intent = Intent(this@SplashActivity, LockActivity::class.java)
                    intent.putExtra("command", "askforpassword")
                    startActivity(intent)
                    finish()*/
                }
            }
        })
    }
}