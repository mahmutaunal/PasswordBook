package com.mahmutalperenunal.passwordbook.ui.password

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.database.PasswordManagerDatabase
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.databinding.ActivityPasswordBinding
import com.mahmutalperenunal.passwordbook.repository.PasswordManagerRepository
import com.mahmutalperenunal.passwordbook.security.EncryptionDecryption
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.factories.CreateEditViewPasswordViewModelProviderFactory
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.ColorHolder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconDrawable
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.widget.AccountHeaderView

class PasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityPasswordBinding
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var viewModel: CreateEditViewPasswordViewModel

    private var doubleBackToExitPressedOnce = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setUpActionBar()

        val database = PasswordManagerDatabase(this)
        val repository = PasswordManagerRepository(database)
        val factory = CreateEditViewPasswordViewModelProviderFactory(repository)

        viewModel = ViewModelProvider(this, factory)[CreateEditViewPasswordViewModel::class.java]

        Log.d("key", EncryptionDecryption().getKey())

        val emptyList = listOf<Entry>()
        viewModel.filteredSearchList.postValue(emptyList)

        binding.passwordBtnNewPassword.setOnClickListener {
            val intent = Intent(this, CreateEditViewPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        findNavController(R.id.password_fragment).addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.passwordFragment -> binding.passwordTopAppBar.title =
                    resources.getString(R.string.app_name)
                //R.id.searchPasswordFragment -> binding.passwordTopAppBar.title = "Search"
                //R.id.favouritePasswordsFragment -> binding.passwordTopAppBar.title = "Profile"
                R.id.generatePasswordFragment -> binding.passwordTopAppBar.title =
                    "Generate Password"
            }

            /*if (destination.id != R.id.searchPasswordFragment) {
                binding.passwordTopAppBar.collapseActionView()
            }*/
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.search -> {

                //findNavController(R.id.password_fragment).navigate(R.id.searchPasswordFragment)

                val myActionMenuItem: MenuItem = item

                val searchView = myActionMenuItem.actionView as androidx.appcompat.widget.SearchView

                /*val v: View = searchView.findViewById(R.id.search_plate)
                v.setBackgroundColor(Color.parseColor("#ffffff"))

                searchView.queryHint = resources.getString(R.string.search)

                val searchText = searchView.findViewById(R.id.search_src_text) as TextView
                searchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

                if (toggle.onOptionsItemSelected(item)) return true

                val searchCloseIcon: ImageView = searchView.findViewById(R.id.search_close_btn) as ImageView
                searchCloseIcon.setImageResource(R.drawable.ic_clear)*/

                searchView.setOnQueryTextListener(object :
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }


                    override fun onQueryTextChange(newText: String?): Boolean {

                        if (newText?.isNotEmpty() == true || newText?.isNotBlank() == true) {
                            viewModel.searchEntries(newText).observe(this@PasswordActivity) {
                                viewModel.filteredSearchList.postValue(it)
                            }
                        } else {
                            val emptyList = listOf<Entry>()
                            viewModel.filteredSearchList.postValue(emptyList)
                        }

                        return false
                    }
                })
            }

            R.id.profile -> {

            }
        }

        return true

    }


    //Setting up Action Bar
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpActionBar() {
        setSupportActionBar(binding.passwordTopAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    //exit application with double click
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            val intentMainExit = Intent(Intent.ACTION_MAIN)
            intentMainExit.addCategory(Intent.CATEGORY_HOME)
            intentMainExit.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentMainExit)
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, R.string.press_back_again_text, Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 3000)

    }

}