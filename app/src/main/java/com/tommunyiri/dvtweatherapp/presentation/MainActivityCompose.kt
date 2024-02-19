package com.tommunyiri.dvtweatherapp.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.presentation.screens.MainScreen
import com.tommunyiri.dvtweatherapp.ui.theme.DVTWeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_DVTWeatherApp)
        enableEdgeToEdge()
        setContent {
            DVTWeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RequestPermissions()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions() {
    val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
    if (!checkAllPermissions(permissionsState, LocalContext.current)) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(
            key1 = lifecycleOwner,
            effect = {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            permissionsState.permissions.forEach { perm ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when (perm.permission) {

                        Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            when {
                                perm.hasPermission -> {
                                    //Text(text = "Location permission accepted")
                                    val navController = rememberNavController()
                                    MainScreen(navController)
                                }

                                perm.shouldShowRationale -> {
                                    Text(
                                        text = "Permissions are needed" +
                                                "to access weather information of your area"
                                    )
                                }

                                perm.isPermanentlyDenied() -> {
                                    Text(
                                        text = "Permissions were permanently" +
                                                "denied. You can enable them in the app" +
                                                "settings."
                                    )
                                }
                            }
                        }
                    }
                } else {
                    when (perm.permission) {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION -> {
                            when {
                                perm.hasPermission -> {
                                    //Text(text = "Location permission accepted")
                                    val navController = rememberNavController()
                                    MainScreen(navController)
                                }

                                perm.shouldShowRationale -> {
                                    Text(
                                        text = "Location permission is needed" +
                                                "to access weather information of your area"
                                    )
                                }

                                perm.isPermanentlyDenied() -> {
                                    Text(
                                        text = "Location permission was permanently" +
                                                "denied. You can enable it in the app" +
                                                "settings."
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    } else {
        val navController = rememberNavController()
        MainScreen(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    DVTWeatherAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreen(navController = navController)
        }
    }
}

/** Permission Checking  */
@OptIn(ExperimentalPermissionsApi::class)
private fun checkAllPermissions(
    permissionState: MultiplePermissionsState,
    context: Context
): Boolean {
    val permissions = permissionState.permissions
    var hasPermissions = true
    for (permission in permissions) {
        hasPermissions = hasPermissions and isPermissionGranted(permission.toString(), context)
    }
    return hasPermissions
}

private fun isPermissionGranted(permission: String, context: Context) =
    ActivityCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED