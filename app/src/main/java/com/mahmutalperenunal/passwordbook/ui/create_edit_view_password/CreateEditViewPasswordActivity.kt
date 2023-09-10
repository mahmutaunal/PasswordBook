package com.mahmutalperenunal.passwordbook.ui.create_edit_view_password

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.PasswordManagerDatabase
import com.mahmutalperenunal.passwordbook.databinding.ActivityCreateEditViewPasswordBinding
import com.mahmutalperenunal.passwordbook.repository.PasswordManagerRepository
import com.mahmutalperenunal.passwordbook.ui.factories.CreateEditViewPasswordViewModelProviderFactory
import com.mahmutalperenunal.passwordbook.ui.password.PasswordActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel

class CreateEditViewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEditViewPasswordBinding
    lateinit var viewModel: CreateEditViewPasswordViewModel

    private lateinit var command: String

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEditViewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val database = PasswordManagerDatabase(this)
        val repository = PasswordManagerRepository(database)
        val factory = CreateEditViewPasswordViewModelProviderFactory(repository)

        viewModel = ViewModelProvider(this, factory)[CreateEditViewPasswordViewModel::class.java]

        command = intent?.getStringExtra("command").toString()
        val data = intent?.getSerializableExtra("data")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.createEditViewPassword_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (command == "view") {
            val bundle = Bundle().apply {
                putSerializable("data", data)
            }
            navController.popBackStack()
            navController.navigate(R.id.viewPasswordsFragment, bundle)
        } else if (command == "edit") {
            val bundle = Bundle().apply {
                putSerializable("data", data)
            }
            navController.popBackStack()
            navController.navigate(R.id.editPasswordFragment, bundle)
        } else {
            navController.popBackStack()
            navController.navigate(R.id.createPasswordFragment)
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent =
            Intent(this, PasswordActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}