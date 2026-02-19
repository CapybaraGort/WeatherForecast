package org.sergey.forecast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SetLocationViewModel @Inject constructor(
) : ViewModel() {

    private val _latitudeInput = MutableStateFlow("")
    val latitudeInput: StateFlow<String> = _latitudeInput.asStateFlow()

    private val _longitudeInput = MutableStateFlow("")
    val longitudeInput: StateFlow<String> = _longitudeInput.asStateFlow()

    val latitude: Double? get() = _latitudeInput.value.toDoubleOrNull()?.takeIf { it in -90.0..90.0 }
    val longitude: Double? get() = _longitudeInput.value.toDoubleOrNull()?.takeIf { it in -180.0..180.0 }

    val latValid: Boolean get() = _latitudeInput.value.isEmpty() || latitude != null
    val lonValid: Boolean get() = _longitudeInput.value.isEmpty() || longitude != null
    private var _radiusMeters: MutableStateFlow<Int> = MutableStateFlow(10000)
    val radiusMeters: StateFlow<Int> get() = _radiusMeters

    fun setLatitudeInput(value: String) { _latitudeInput.value = value }
    fun setLongitudeInput(value: String) { _longitudeInput.value = value }

    fun setRadiusMeters(value: Int) {
        _radiusMeters.update { value }
    }
}