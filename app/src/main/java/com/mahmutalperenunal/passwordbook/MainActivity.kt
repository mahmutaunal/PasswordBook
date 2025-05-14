package com.mahmutalperenunal.passwordbook

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mahmutalperenunal.passwordbook.databinding.ActivityMainBinding
import com.mahmutalperenunal.passwordbook.utils.LocalizationHelper
import com.mahmutalperenunal.passwordbook.utils.RootCheckUtil
import com.mahmutalperenunal.passwordbook.utils.ThemeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        if (RootCheckUtil.isDeviceRooted(this)) {
            Toast.makeText(this, resources.getString(R.string.the_app_cannot_be_run_because_the_device_is_rooted), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        ThemeHelper.applyTheme(this)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun attachBaseContext(newBase: Context) {
        val localizedContext = LocalizationHelper.applySavedLocale(newBase)
        super.attachBaseContext(localizedContext)
    }
}