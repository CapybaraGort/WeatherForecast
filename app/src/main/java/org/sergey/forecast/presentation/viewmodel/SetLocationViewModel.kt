package org.sergey.forecast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.sergey.forecast.domain.repository.MeteostatRepository
import javax.inject.Inject

@HiltViewModel
class SetLocationViewModel @Inject constructor(
    private val repository: MeteostatRepository
) : ViewModel() {

    private var _latitude: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> get() = _latitude

    private var _longitude: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> get() = _longitude

    private var _radiusMeters: MutableStateFlow<Int> = MutableStateFlow(10)
    val radiusMeters: StateFlow<Int> get() = _radiusMeters

    fun setLatitude(value: Double?) {
        if(value != null && value in -90.0..90.0) {
            _latitude.update { value }
        }
    }
    fun setLongitude(value: Double?) {
        if(value != null && value in -180.0..180.0) {
            _longitude.update { value }
        }
    }

    fun setRadiusMeters(value: Int) {
        _radiusMeters.update { value }
    }
}