package com.mahmutalperenunal.passwordbook.ui.lock

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.PasswordManagerDatabase
import com.mahmutalperenunal.passwordbook.databinding.ActivityLockBinding
import com.mahmutalperenunal.passwordbook.repository.PasswordManagerRepository
import com.mahmutalperenunal.passwordbook.ui.factories.CreateEditViewPasswordViewModelProviderFactory
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding
    lateinit var viewModel: CreateEditViewPasswordViewModel

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val database = PasswordManagerDatabase(this)
        val repository = PasswordManagerRepository(database)
        val factory = CreateEditViewPasswordViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CreateEditViewPasswordViewModel::class.java]

        val command = intent.getStringExtra("command")

        if (command == "createPassword") {
            findNavController(R.id.lock_fragment).popBackStack()
            //findNavController(R.id.lock_fragment).navigate(R.id.createLockFragment)
        }

        if (command == "askForPassword") {
            findNavController(R.id.lock_fragment).popBackStack()
            //findNavController(R.id.lock_fragment).navigate(R.id.lockPasswordFragment)
        }

        if (command == "changePassword") {
            findNavController(R.id.lock_fragment).popBackStack()
            //findNavController(R.id.lock_fragment).navigate(R.id.updateLockPasswordFragment)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, PasswordActivity::class.java)
        startActivity(intent)
        finish()
    }
}