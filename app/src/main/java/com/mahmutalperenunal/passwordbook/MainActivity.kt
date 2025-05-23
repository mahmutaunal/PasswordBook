package com.mahmutalperenunal.passwordbook

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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

        val barColor = getColor(R.color.background_color)
        window.statusBarColor = barColor
        window.navigationBarColor = barColor

        WindowCompat.getInsetsController(window, window.decorView).apply {
            val isDark = resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES

            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
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