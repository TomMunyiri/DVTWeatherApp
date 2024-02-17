package com.tommunyiri.dvtweatherapp.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.databinding.ActivityMainBinding
import com.tommunyiri.dvtweatherapp.utils.makeGone
import com.tommunyiri.dvtweatherapp.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_DVTWeatherApp)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //enableEdgeToEdge()
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.mainNavFragment)
        setupActionBarWithNavController(navController)
        binding.bottomNavBar.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.mainNavFragment).navigateUp()

    fun hideToolBar() {
        binding.toolbar.makeGone()
    }

    fun showToolBar() {
        binding.toolbar.makeVisible()
    }

    fun setTransparentStatusBar() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightStatusBars = false
    }

    fun resetTransparentStatusBar() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        //window.statusBarColor = Color.GRAY
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        windowInsetController?.isAppearanceLightStatusBars =
            defaultNightMode != AppCompatDelegate.MODE_NIGHT_YES
    }

    fun hideBottomNavigationBar() {
        binding.bottomNavBar.makeGone()
    }

    fun showBottomNavigationBar() {
        binding.bottomNavBar.makeVisible()
    }
}