package org.sergey.forecast.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.sergey.forecast.R
import org.sergey.forecast.core.ui.UiState
import org.sergey.forecast.domain.entity.DailyWeather
import org.sergey.forecast.domain.entity.StationMeta
import org.sergey.forecast.presentation.viewmodel.ForecastUiState
import org.sergey.forecast.presentation.viewmodel.ForecastViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = hiltViewModel(),
    navToSetLocation: () -> Unit = { }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val pullRefreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Меню",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    label = { Text("Сменить метеостанцию") },
                    selected = false,
                    onClick = navToSetLocation
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Прогноз") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.menu),
                                contentDescription = "Open menu"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = state.dailyUiState is UiState.Loading || state.stationMetaState is UiState.Loading,
                onRefresh = {
                    viewModel.loadStationMeta()
                    viewModel.loadDailyWeather()
                },
                state = pullRefreshState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                ForecastContent(
                    state = state,
                    onDateStartChange = viewModel::updateDateStart,
                    onDateEndChange = viewModel::updateDateEnd,
                    onLoadClick = viewModel::loadDailyWeather,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ForecastContent(
    state: ForecastUiState,
    onDateStartChange: (String) -> Unit,
    onDateEndChange: (String) -> Unit,
    onLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn (
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StationMetaSection(
                state = state.stationMetaState
            )
        }
        item {
            DateRangeSection(
                dateStart = state.dateStart,
                dateEnd = state.dateEnd,
                onDateStartChange = onDateStartChange,
                onDateEndChange = onDateEndChange,
                onLoadClick = onLoadClick,
            )
        }
        dailySection(state.dailyUiState)
    }
}

@Composable
private fun StationMetaSection(state: UiState<StationMeta>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Данные метеостанции",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        when (state) {
            is UiState.Idle -> { }
            is UiState.Loading ->
                CircularProgressIndicator()
            is UiState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error
            )
            is UiState.Success -> {
                var expanded by rememberSaveable { mutableStateOf(false) }
                val m = state.data
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Станция: ${m.name?.values?.first() ?: m.stationId}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = if (expanded) painterResource(R.drawable.arrow_up)
                                else painterResource(R.drawable.arrow_down),
                                contentDescription = if (expanded) "Свернуть" else "Развернуть"
                            )
                        }
                        AnimatedVisibility(visible = expanded) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LabelValue("ID", m.stationId)
                                LabelValue("Страна", m.country ?: "—")
                                LabelValue("Регион", m.region ?: "—")
                                LabelValue("National ID", m.nationalId ?: "—")
                                LabelValue("WMO ID", m.wmoId ?: "—")
                                LabelValue("ICAO ID", m.icaoId ?: "—")
                                LabelValue("Широта", m.latitude?.toString() ?: "—")
                                LabelValue("Долгота", m.longitude?.toString() ?: "—")
                                LabelValue("Высота (м)", m.elevation?.toString() ?: "—")
                                LabelValue("Часовой пояс", m.timezone ?: "—")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DateRangeSection(
    dateStart: String,
    dateEnd: String,
    onDateStartChange: (String) -> Unit,
    onDateEndChange: (String) -> Unit,
    onLoadClick: () -> Unit,
) {
    var showDateRangePicker by remember { mutableStateOf(false) }

    val now = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    }
    val earliest = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.YEAR, -9)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayedMonthMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = utcTimeMillis
                }

                return !date.before(earliest) && !date.after(now)
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                return year >= currentYear - 9 && year <= currentYear
            }
        }
    )

    val formatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Период данных",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = dateStart)
            Text("—")
            Text(dateEnd)
            IconButton(
                onClick = {
                    showDateRangePicker = true
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = "Date range"
                )
            }
        }

        Button(
            onClick = onLoadClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Загрузить")
        }
    }

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis
                        if (startMillis != null) {
                            onDateStartChange(formatter.format(Date(startMillis)))
                        }
                        if (endMillis != null) {
                            onDateEndChange(formatter.format(Date(endMillis)))
                        }
                        showDateRangePicker = false
                    },
                    enabled = dateRangePickerState.selectedStartDateMillis != null &&
                            dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("Применить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                title = {
                    Text(
                        text = "Выберите период",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )
                },
                headline = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dateRangePickerState.selectedStartDateMillis
                                ?.let { formatter.format(Date(it)) }
                                ?: "...",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text("—")
                        Text(
                            text = dateRangePickerState.selectedEndDateMillis
                                ?.let { formatter.format(Date(it)) }
                                ?: "...",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                showModeToggle = false
            )
        }
    }
}

fun LazyListScope.dailySection(state: UiState<List<DailyWeather>>) {
    item {
        Text(
            text = "Данные по дням",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
    when (state) {
        is UiState.Idle -> item {
            Text(
                text = "Укажите период и нажмите «Загрузить»",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        is UiState.Loading -> item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Error -> item {
            Text(text = state.message, color = MaterialTheme.colorScheme.error)
        }
        is UiState.Success -> {
            if (state.data.isEmpty()) {
                item {
                    Text(
                        text = "Не найдено данных за указанный период",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(items = state.data, key = { it.date }) { item ->
                    DailyWeatherCard(item)
                }
            }
        }
    }
}

@Composable
private fun DailyWeatherCard(day: DailyWeather) {
    val dateOnly = day.date.substringBefore(" ")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(day.isEmpty()) {
                Text("$dateOnly — Нет данных")
            } else {
                Column {
                    Text(text = dateOnly, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Осадки: ${day.prcp ?: "-"} мм", style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Средняя: ${day.tavg?.toInt() ?: "-"}°")
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Мин: ${day.tmin?.toInt() ?: "-"}°")
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Макс: ${day.tmax?.toInt() ?: "-"}°")
                }
            }
        }
    }
}
