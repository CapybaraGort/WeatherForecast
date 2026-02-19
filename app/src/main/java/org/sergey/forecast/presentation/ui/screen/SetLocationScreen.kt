package org.sergey.forecast.presentation.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.sergey.forecast.R
import org.sergey.forecast.presentation.viewmodel.SetLocationViewModel

@Composable
fun SetLocationScreen(
    viewModel: SetLocationViewModel = hiltViewModel(),
    navToNearbyStations: (Double, Double, Int) -> Unit
) {
    val context = LocalContext.current
    val latitudeInput by viewModel.latitudeInput.collectAsStateWithLifecycle()
    val longitudeInput by viewModel.longitudeInput.collectAsStateWithLifecycle()

    val radiusMeters by viewModel.radiusMeters.collectAsStateWithLifecycle()
    val radiusOptions: List<Int> = listOf(1000, 2500, 5000, 10000)

    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun fetchLocation() {
        try {
            locationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.setLatitudeInput(location.latitude.toString())
                    viewModel.setLongitudeInput(location.longitude.toString())
                } else {
                    locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { fresh ->
                            if(fresh == null)
                                Toast.makeText(context, "Включите местоположение", Toast.LENGTH_SHORT).show()
                            else {
                                viewModel.setLatitudeInput(fresh.latitude.toString())
                                viewModel.setLongitudeInput(fresh.longitude.toString())
                            }
                        }
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

    Scaffold(
        modifier = Modifier.imePadding(),
        floatingActionButton = {
            IconButton(
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = { requestLocation() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.location),
                    contentDescription = "locate me"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = latitudeInput,
                onValueChange = {
                    viewModel.setLatitudeInput(it)
                                },
                label = { Text("Широта") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = !viewModel.latValid
            )
            OutlinedTextField(
                value = longitudeInput,
                onValueChange = {
                    viewModel.setLongitudeInput(it)
                                },
                label = { Text("Долгота") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = !viewModel.lonValid
            )
            Text("Радиус поиска метеостанций")
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
                        Text("${meters / 1000} км", textAlign = TextAlign.Center)
                    }
                }
            }
            Button(
                onClick = {
                    navToNearbyStations(viewModel.latitude ?: 0.0, viewModel.longitude ?: 0.0, radiusMeters)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = latitudeInput.isNotEmpty() && longitudeInput.isNotEmpty()
                        && viewModel.latValid && viewModel.lonValid
            ) {
                Text("Далее")
            }
        }
    }
}
