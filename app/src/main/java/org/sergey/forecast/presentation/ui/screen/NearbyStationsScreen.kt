package org.sergey.forecast.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.sergey.forecast.R
import org.sergey.forecast.presentation.ui.component.MeteostationCard
import org.sergey.forecast.presentation.ui.component.ReloadButton
import org.sergey.forecast.presentation.viewmodel.NearbyStationsUiState
import org.sergey.forecast.presentation.viewmodel.NearbyStationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyStationsScreen(
    navToSetLocation: () -> Unit = { },
    navToForecast: (String) -> Unit = { },
    viewModel: NearbyStationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Метеостанции") },
                navigationIcon = {
                    IconButton(navToSetLocation) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when(val state = uiState) {
            is NearbyStationsUiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message)
                        ReloadButton(
                            text = "Обновить",
                            onClick = viewModel::reloadStations,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
            NearbyStationsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NearbyStationsUiState.Success -> {
                if(state.stations.isNotEmpty())
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding).padding(horizontal = 6.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(
                            items = state.stations,
                            key = { it.id }
                        ) { station ->
                            MeteostationCard(
                                station = station,
                                onClick = { id ->
                                    navToForecast(id)
                                }
                            )
                        }
                    }
                else
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
                    ) {
                        Text("Метеостанции не найдены. Попробуйте другие координаты", textAlign = TextAlign.Center)
                    }
            }
        }
    }
}