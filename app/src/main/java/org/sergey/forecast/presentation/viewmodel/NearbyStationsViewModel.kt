package org.sergey.forecast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sergey.forecast.domain.entity.Station
import org.sergey.forecast.domain.repository.StationRepository
import javax.inject.Inject

@HiltViewModel
class NearbyStationsViewModel @Inject constructor(
    private val stationRepository: StationRepository
) : ViewModel() {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var radiusMeters: Int = 0

    fun setupCoordinates(lat: Double, lon: Double, radius: Int) {
        latitude = lat
        longitude = lon
        radiusMeters = radius
    }

    private val _uiState = MutableStateFlow<NearbyStationsUiState>(NearbyStationsUiState.Loading)

    val uiState: StateFlow<NearbyStationsUiState> = _uiState
        .asStateFlow()
        .onStart { loadStations() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = NearbyStationsUiState.Loading
        )

    fun reloadStations() {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            _uiState.update { NearbyStationsUiState.Loading }
            stationRepository.getNearbyStations(latitude, longitude, radiusMeters, 20)
                .onSuccess { list ->
                    _uiState.update { NearbyStationsUiState.Success(list) }
                }
                .onFailure {
                    _uiState.update { NearbyStationsUiState.Error("Метеостанций не найдено. Повторите поиск") }
                }
        }
    }
}

sealed class NearbyStationsUiState {
    data object Loading : NearbyStationsUiState()
    data class Success(val stations: List<Station>) : NearbyStationsUiState()
    data class Error(val message: String) : NearbyStationsUiState()
}