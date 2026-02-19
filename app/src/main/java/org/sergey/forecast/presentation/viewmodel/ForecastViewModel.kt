package org.sergey.forecast.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sergey.forecast.core.ui.UiState
import org.sergey.forecast.domain.entity.DailyWeather
import org.sergey.forecast.domain.entity.StationMeta
import org.sergey.forecast.domain.repository.WeatherRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private var stationId: String = ""
    private var dailyCacheJob: Job? = null
    private var metaCacheJob: Job? = null

    private var stationLatitude = 0.0
    private var stationLongitude = 0.0


    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState
        .asStateFlow()
        .onStart {
            loadStationMeta()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, ForecastUiState())


    fun setStationId(id: String) {
        if (stationId == id) return
        stationId = id
        dailyCacheJob?.cancel()
        if (id.isEmpty()) return
        _uiState.update {
            it.copy(
                dateStart = defaultStart(),
                dateEnd = defaultEnd(),
                dailyUiState = UiState.Idle
            )
        }
    }

    fun loadStationMeta() {
        if (stationId.isEmpty()) return

        metaCacheJob?.cancel()
        _uiState.update { it.copy(stationMetaState = UiState.Loading) }

        metaCacheJob = viewModelScope.launch {
            repository.getStationMetaCache(stationId)
                .catch { }
                .collect { meta ->
                    if (meta != null) {
                        stationLatitude = meta.latitude ?: 0.0
                        stationLongitude = meta.longitude ?: 0.0
                        _uiState.update { it.copy(stationMetaState = UiState.Success(meta)) }
                    }
                }
        }
        viewModelScope.launch {
            delay(50)
            val result = repository.fetchStationMeta(stationId)
            metaCacheJob?.cancel()
            _uiState.update {
                result.fold(
                    onSuccess = { data ->
                        if (data != null) {
                            stationLatitude = data.latitude ?: 0.0
                            stationLongitude = data.longitude ?: 0.0
                            it.copy(stationMetaState = UiState.Success(data))
                        }
                        else it.copy(stationMetaState = UiState.Error("Нет данных"))
                    },
                    onFailure = { _ ->
                        if (it.stationMetaState is UiState.Success) it
                        else it.copy(stationMetaState = UiState.Error("Нет соединения"))
                    }
                )
            }
        }
    }

    private fun defaultStart(): String = LocalDate.now().format(formatter)
    private fun defaultEnd(): String = LocalDate.now().plusDays(1).format(formatter)

    fun updateDateStart(value: String) {
        _uiState.update { it.copy(dateStart = value) }
    }

    fun updateDateEnd(value: String) {
        _uiState.update { it.copy(dateEnd = value) }
    }


    fun loadDailyWeather() {
        if (stationId.isEmpty()) return
        val start = _uiState.value.dateStart
        val end = _uiState.value.dateEnd
        if (start.isBlank() || end.isBlank()) return

        dailyCacheJob?.cancel()
        _uiState.update { it.copy(dailyUiState = UiState.Loading) }

        dailyCacheJob = viewModelScope.launch {
            repository.getDailyWeatherCache(stationId, start, end)
                .catch { }
                .collect { cached ->
                    val merged = mergeDailyDataWithRange(cached, start, end)
                    if (merged.isNotEmpty()) {
                        _uiState.update { it.copy(dailyUiState = UiState.Success(merged)) }
                    } else {
                        _uiState.update { it.copy(dailyUiState = UiState.Success(emptyList())) }
                    }
                }
        }

        viewModelScope.launch {
            delay(50)
            val result = repository.getDailyWeather(stationId, stationLatitude, stationLongitude, start, end)
            dailyCacheJob?.cancel()
            _uiState.update {
                result.fold(
                    onSuccess = { data ->
                        it.copy(dailyUiState = UiState.Success(mergeDailyDataWithRange(data, start, end)))
                    },
                    onFailure = { _ ->
                        if (it.dailyUiState is UiState.Success) it
                        else it.copy(dailyUiState = UiState.Error("Нет соединения"))
                    }
                )
            }
        }
    }

    private fun mergeDailyDataWithRange(
        cachedData: List<DailyWeather>,
        start: String,
        end: String
    ): List<DailyWeather> {
        val startDate = LocalDate.parse(start, formatter)
        val endDate = LocalDate.parse(end, formatter)
        val cachedMap = cachedData.associateBy { it.date }

        return generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .map { cachedMap[it.format(formatter)] }
            .toList()
            .filterNotNull()
    }
}

data class ForecastUiState(
    val stationMetaState: UiState<StationMeta> = UiState.Loading,
    val dailyUiState: UiState<List<DailyWeather>> = UiState.Idle,
    val dateStart: String = "",
    val dateEnd: String = ""
)