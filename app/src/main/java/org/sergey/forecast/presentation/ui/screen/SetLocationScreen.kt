package org.sergey.forecast.presentation.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.sergey.forecast.presentation.viewmodel.SetLocationViewModel

@Composable
fun SetLocationScreen(
    onNext: () -> Unit = {},
    viewModel: SetLocationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val latitude by viewModel.latitude.collectAsStateWithLifecycle()
    val longitude by viewModel.longitude.collectAsStateWithLifecycle()
    val radiusMeters by viewModel.radiusMeters.collectAsStateWithLifecycle()
    val radiusOptions: List<Int> = listOf(1000, 2500, 5000, 10000)

    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun fetchLocation() {
        try {
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        viewModel.setLatitude(it.latitude)
                        viewModel.setLongitude(it.longitude)
                    }
                }
        } catch (_: SecurityException) {}
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchLocation()
        }
    }

    fun requestLocation() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                fetchLocation()
            }
            else -> permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val latValid = latitude in -90.0..90.0
    val lonValid = longitude in -180.0..180.0

    Scaffold(

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Местоположение")
            Button(
                onClick = { requestLocation() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Определить местоположение")
            }
            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = { viewModel.setLatitude(it.toDoubleOrNull()) },
                label = { Text("Широта") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = !latValid
            )
            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = { viewModel.setLongitude(it.toDoubleOrNull()) },
                label = { Text("Долгота") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = !lonValid
            )
            Text("Радиус поиска метеостанций (км)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                radiusOptions.forEach { meters ->
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor =
                                if(radiusMeters == meters)
                                    MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.onSurface,
                            containerColor =
                                if(radiusMeters == meters)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = { viewModel.setRadiusMeters(meters)},
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${meters / 100} км")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = latValid && lonValid
            ) {
                Text("Далее")
            }
        }
    }
}
