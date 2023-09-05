package com.mahmutalperenunal.passwordbook.ui.password

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.appcompat.widget.SearchView
import com.mahmutalperenunal.passwordbook.R
import com.mahmutalperenunal.passwordbook.adapter.PasswordAdapter
import com.mahmutalperenunal.passwordbook.database.PasswordManagerDatabase
import com.mahmutalperenunal.passwordbook.database.entities.Entry
import com.mahmutalperenunal.passwordbook.databinding.ActivityPasswordBinding
import com.mahmutalperenunal.passwordbook.repository.PasswordManagerRepository
import com.mahmutalperenunal.passwordbook.ui.create_edit_view_password.CreateEditViewPasswordActivity
import com.mahmutalperenunal.passwordbook.ui.factories.CreateEditViewPasswordViewModelProviderFactory
import com.mahmutalperenunal.passwordbook.ui.lock.LockActivity
import com.mahmutalperenunal.passwordbook.ui.viewmodels.CreateEditViewPasswordViewModel
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.ColorHolder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.iconDrawable
import com.mikepenz.materialdrawer.model.interfaces.nameRes
import com.mikepenz.materialdrawer.util.updateItem

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

        binding.passwordBtnNewPassword.visibility = View.VISIBLE

        val database = PasswordManagerDatabase(this)
        val repository = PasswordManagerRepository(database)
        val factory = CreateEditViewPasswordViewModelProviderFactory(repository)

        viewModel = ViewModelProvider(this, factory)[CreateEditViewPasswordViewModel::class.java]

        val emptyList = listOf<Entry>()
        viewModel.filteredSearchList.postValue(emptyList)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.password_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.passwordBtnNewPassword.setOnClickListener {
            val intent = Intent(this, CreateEditViewPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.passwordFragment -> binding.passwordTopAppBar.title =
                    resources.getString(R.string.app_name)

                R.id.searchPasswordFragment -> binding.passwordTopAppBar.title =
                    getString(R.string.search)

                R.id.favouritePasswordFragment -> binding.passwordTopAppBar.title =
                    getString(R.string.favourites_text)

                R.id.generatePasswordFragment -> binding.passwordTopAppBar.title =
                    getString(R.string.generate_password_text)
            }

            if (destination.id != R.id.searchPasswordFragment) {
                binding.passwordTopAppBar.collapseActionView()
            }
        }

        updateDrawerMenuBatch()

    }

    override fun onPause() {
        super.onPause()
        binding.passwordNavView.setSelection(1, true)
    }

    override fun onResume() {
        super.onResume()
        binding.passwordNavView.setSelection(1, true)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateDrawerMenuBatch() {

        val item1 = PrimaryDrawerItem().apply {
            nameRes = R.string.all_text
            identifier = 1
            iconDrawable = resources.getDrawable(R.drawable.ic_all)

            badgeStyle = BadgeStyle().apply {
                textColor = ColorHolder.fromColor(Color.WHITE)
                color = ColorHolder.fromColor(Color.parseColor("#EF5350"))
                corners = DimenHolder.fromDp(50)
            }
        }

        val item2 = PrimaryDrawerItem().apply {
            nameRes = R.string.social_text
            identifier = 2
            iconDrawable = resources.getDrawable(R.drawable.ic_social)

            badgeStyle = BadgeStyle().apply {
                textColor = ColorHolder.fromColor(Color.WHITE)
                color = ColorHolder.fromColor(Color.parseColor("#3D5AFE"))
                corners = DimenHolder.fromDp(50)
            }
        }

        val item3 = PrimaryDrawerItem().apply {
            nameRes = R.string.mails_text
            identifier = 3
            iconDrawable = resources.getDrawable(R.drawable.ic_mail)

            badgeStyle = BadgeStyle().apply {
                textColor = ColorHolder.fromColor(Color.WHITE)
                color = ColorHolder.fromColor(Color.parseColor("#8E24AA"))
                corners = DimenHolder.fromDp(50)
            }
        }

        val item4 = PrimaryDrawerItem().apply {
            nameRes = R.string.cards_text
            identifier = 4
            iconDrawable = resources.getDrawable(R.drawable.ic_card)

            badgeStyle = BadgeStyle().apply {
                textColor = ColorHolder.fromColor(Color.WHITE)
                color = ColorHolder.fromColor(Color.parseColor("#29B6F6"))
                corners = DimenHolder.fromDp(50)
            }
        }

        val item5 = PrimaryDrawerItem().apply {
            nameRes = R.string.work_text
            identifier = 5
            iconDrawable = resources.getDrawable(R.drawable.ic_work)

            badgeStyle = BadgeStyle().apply {
                textColor = ColorHolder.fromColor(Color.WHITE)
                color = ColorHolder.fromColor(Color.parseColor("#7CB342"))
                corners = DimenHolder.fromDp(50)
            }
        }

        val item6 = PrimaryDrawerItem().apply {
            nameRes = R.string.others_text
            identifier = 6
            iconDrawable = resources.getDrawable(R.drawable.ic_others)

            badgeStyle = BadgeStyle().apply {
                textColor = ColorHolder.fromColor(Color.WHITE)
                color = ColorHolder.fromColor(Color.parseColor("#FFA100"))
                corners = DimenHolder.fromDp(50)
            }
        }

        val item7 = PrimaryDrawerItem().apply {
            nameRes = R.string.change_password_text
            identifier = 7
            iconDrawable = resources.getDrawable(R.drawable.ic_password_change)
            isSelectable = false
        }

        val item8 = PrimaryDrawerItem().apply {
            nameRes = R.string.generate_password_text
            identifier = 8
            iconDrawable = resources.getDrawable(R.drawable.ic_generate_password)
            isSelectable = false
        }

        val item9 = PrimaryDrawerItem().apply {
            nameRes = R.string.exit_text
            identifier = 9
            iconDrawable = resources.getDrawable(R.drawable.ic_exit)
            isSelectable = false
        }

        viewModel.getAllEntries().observe(this) {
            viewModel.sortedList.postValue(it)
            if (it.isNotEmpty()) {
                item1.apply {
                    badge = StringHolder("${it.size}")
                    binding.passwordNavView.updateItem(item1)
                }
            }
        }


        viewModel.sortEntries("Social").observe(this) {
            if (it.isNotEmpty()) {
                item2.apply {
                    badge = StringHolder("${it.size}")
                    binding.passwordNavView.updateItem(item2)
                }
            }
        }

        viewModel.sortEntries("Mails").observe(this) {
            if (it.isNotEmpty()) {
                item3.apply {
                    badge = StringHolder("${it.size}")
                    binding.passwordNavView.updateItem(item3)
                }
            }
        }

        viewModel.sortEntries("Cards").observe(this) {
            if (it.isNotEmpty()) {
                item4.apply {
                    badge = StringHolder("${it.size}")
                    binding.passwordNavView.updateItem(item4)
                }
            }
        }

        viewModel.sortEntries("Work").observe(this) {
            if (it.isNotEmpty()) {
                item5.apply {
                    badge = StringHolder("${it.size}")
                    binding.passwordNavView.updateItem(item5)
                }
            }
        }

        viewModel.sortEntries("Other").observe(this) {
            if (it.isNotEmpty()) {
                item6.apply {
                    badge = StringHolder("${it.size}")
                    binding.passwordNavView.updateItem(item6)
                }
            }
        }

        binding.passwordNavView.itemAdapter.removeRange(0,6)
        binding.passwordNavView.itemAdapter.add(
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            DividerDrawerItem(),
            item7,
            item8,
            item9
        )

        // specify a click listener
        binding.passwordNavView.onDrawerItemClickListener = { _, _, position ->
            when(position) {
                0 -> {
                    viewModel.getAllEntries().observe(this) {
                        viewModel.sortedList.postValue(it)
                        item1.apply {
                            badge = StringHolder("${it.size}")
                        }
                        binding.passwordNavView.updateItem(item1)
                    }
                    supportActionBar?.title = getString(R.string.app_name)
                }
                1 -> {
                    viewModel.sortEntries("Social").observe(this) {
                        viewModel.sortedList.postValue(it)
                    }
                    supportActionBar?.title = getString(R.string.social_text)
                }
                2 -> {
                    viewModel.sortEntries("Mails").observe(this) {
                        viewModel.sortedList.postValue(it)
                    }
                    supportActionBar?.title = getString(R.string.mails_text)
                }
                3 -> {
                    viewModel.sortEntries("Cards").observe(this) {
                        viewModel.sortedList.postValue(it)
                    }
                    supportActionBar?.title = getString(R.string.cards_text)
                }
                4 -> {
                    viewModel.sortEntries("Work").observe(this) {
                        viewModel.sortedList.postValue(it)
                    }
                    supportActionBar?.title = getString(R.string.work_text)
                }
                5 -> {
                    viewModel.sortEntries("Other").observe(this) {
                        viewModel.sortedList.postValue(it)
                    }
                    supportActionBar?.title = getString(R.string.others_text)
                }

                //position 6 is divider

                7 -> {
                    val intent = Intent(this, LockActivity::class.java)
                    intent.putExtra("command", "changePassword")
                    startActivity(intent)
                }

                8 -> {
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.password_fragment) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.popBackStack()
                    navController.navigate(R.id.generatePasswordFragment)

                    supportActionBar?.setDisplayHomeAsUpEnabled(false)

                    binding.passwordBtnNewPassword.visibility = View.GONE
                }

                9 -> {
                    // Closes the app and removes it from android tasks on device
                    finishAndRemoveTask()
                }

            }

            false
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.search -> {

                val navHostFragment = supportFragmentManager.findFragmentById(R.id.password_fragment) as NavHostFragment
                val navController = navHostFragment.navController

                navController.navigate(R.id.searchPasswordFragment)

                val myActionMenuItem: MenuItem = item

                val searchView = myActionMenuItem.actionView as SearchView

                searchView.queryHint = resources.getString(R.string.search)

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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

                searchView.setOnCloseListener {
                    val intent = Intent(this@PasswordActivity, PasswordActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }

                binding.passwordBtnNewPassword.visibility = View.GONE
            }

            R.id.favourites -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.password_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.popBackStack()
                navController.navigate(R.id.favouritePasswordFragment)

                supportActionBar?.setDisplayHomeAsUpEnabled(false)

                binding.passwordBtnNewPassword.visibility = View.GONE
            }
        }

        return true

    }


    //Setting up Action Bar
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpActionBar() {
        setSupportActionBar(binding.passwordTopAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(
            this,
            binding.passwordDrawerLayout,
            binding.passwordTopAppBar,
            R.string.open_text,
            R.string.close_text
        )
        binding.passwordDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    //exit application with double click
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        when (binding.passwordTopAppBar.title) {
            getString(R.string.app_name) -> {

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
            getString(R.string.search) -> {

                val intent = Intent(this@PasswordActivity, PasswordActivity::class.java)
                startActivity(intent)
                finish()

            }
            getString(R.string.favourites_text) -> {

                val intent = Intent(this@PasswordActivity, PasswordActivity::class.java)
                startActivity(intent)
                finish()

            }
            else -> {

                val intent = Intent(this@PasswordActivity, PasswordActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

    }

}