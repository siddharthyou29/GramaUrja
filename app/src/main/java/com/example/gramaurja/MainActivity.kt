package com.example.gramaurja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

// --- Theme Colors ---
val DarkGreen = Color(0xFF1B5E20)
val BrightAmber = Color(0xFFFFB300)
val DarkBackground = Color(0xFF121212)
val LightText = Color(0xFFFFFFFF)

// --- Models & Repository ---
data class PowerState(
    val isPowerOn: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

class GramaUrjaRepository {
    private val _powerState = MutableStateFlow(PowerState())
    val powerState = _powerState.asStateFlow()

    suspend fun togglePower(isOn: Boolean) {
        // Simulate real-time network delay within 2 seconds
        delay(1200) 
        _powerState.update { it.copy(isPowerOn = isOn, lastUpdated = System.currentTimeMillis()) }
    }
}

// --- ViewModel ---
class GramaUrjaViewModel : ViewModel() {
    private val repository = GramaUrjaRepository()
    val powerState = repository.powerState
    
    // UI state for loading
    var isUpdating by mutableStateOf(false)
        private set

    fun togglePower(isOn: Boolean) {
        viewModelScope.launch {
            isUpdating = true
            repository.togglePower(isOn)
            isUpdating = false
        }
    }
}

// --- MainActivity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = DarkBackground,
                    surface = Color(0xFF1E1E1E),
                    primary = DarkGreen,
                    secondary = BrightAmber,
                    onBackground = LightText,
                    onSurface = LightText
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GramaUrjaApp()
                }
            }
        }
    }
}

@Composable
fun GramaUrjaApp(viewModel: GramaUrjaViewModel = viewModel()) {
    val powerState by viewModel.powerState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "GRAMA URJA",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BrightAmber,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Zone Selection
        ZoneSelector()

        // Power Status Toggle
        PowerStatusSection(
            isPowerOn = powerState.isPowerOn,
            lastUpdated = powerState.lastUpdated,
            isUpdating = viewModel.isUpdating,
            onToggle = { viewModel.togglePower(it) }
        )

        Divider(color = Color.DarkGray, thickness = 2.dp)

        // Pump Timer
        PumpTimerSection()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelector() {
    val zones = listOf("Village Transformer A", "River Pump Zone", "North Field Line")
    var expanded by remember { mutableStateOf(false) }
    var selectedZone by remember { mutableStateOf(zones[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedZone,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Zone", fontSize = 18.sp, color = BrightAmber) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LightText),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BrightAmber,
                unfocusedBorderColor = Color.White
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkBackground)
        ) {
            zones.forEach { zone ->
                DropdownMenuItem(
                    text = { Text(zone, fontSize = 20.sp, color = LightText) },
                    onClick = {
                        selectedZone = zone
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PowerStatusSection(
    isPowerOn: Boolean,
    lastUpdated: Long,
    isUpdating: Boolean,
    onToggle: (Boolean) -> Unit
) {
    // Dynamic freshness calculation
    var freshnessText by remember { mutableStateOf("Just now") }
    
    LaunchedEffect(lastUpdated) {
        while (true) {
            val diff = System.currentTimeMillis() - lastUpdated
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            freshnessText = when {
                minutes < 1 -> "Updated just now"
                minutes == 1L -> "Updated 1 min ago"
                else -> "Updated $minutes mins ago"
            }
            delay(60000) // Update every minute
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isPowerOn) DarkGreen else Color(0xFF212121)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isPowerOn) "POWER IS ON" else "POWER IS OFF",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = if (isPowerOn) Color.White else BrightAmber,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { onToggle(!isPowerOn) },
                enabled = !isUpdating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPowerOn) Color.Red else DarkGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = if (isPowerOn) "TURN OFF" else "TURN ON",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = freshnessText,
                fontSize = 20.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class Crop(val name: String, val hours: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PumpTimerSection() {
    val crops = listOf(
        Crop("Rice", 6),
        Crop("Sugarcane", 8),
        Crop("Maize", 4)
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedCrop by remember { mutableStateOf(crops[0]) }
    
    var timeRemainingSeconds by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, timeRemainingSeconds) {
        if (isTimerRunning && timeRemainingSeconds > 0) {
            delay(1000)
            timeRemainingSeconds--
            if (timeRemainingSeconds <= 0) {
                isTimerRunning = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PUMP TIMER",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = BrightAmber
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = "${selectedCrop.name} (${selectedCrop.hours} hrs)",
                onValueChange = {},
                readOnly = true,
                label = { Text("Crop Type", fontSize = 16.sp, color = BrightAmber) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, color = LightText),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = BrightAmber,
                    unfocusedBorderColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DarkBackground)
            ) {
                crops.forEach { crop ->
                    DropdownMenuItem(
                        text = { Text("${crop.name} (${crop.hours} hrs)", fontSize = 20.sp, color = LightText) },
                        onClick = {
                            selectedCrop = crop
                            expanded = false
                            if (!isTimerRunning) {
                                timeRemainingSeconds = crop.hours * 3600L
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isTimerRunning || timeRemainingSeconds > 0) {
            val hours = timeRemainingSeconds / 3600
            val minutes = (timeRemainingSeconds % 3600) / 60
            val seconds = timeRemainingSeconds % 60
            
            Text(
                text = "%02d:%02d:%02d".format(hours, minutes, seconds),
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isTimerRunning) {
                Button(
                    onClick = { isTimerRunning = false; timeRemainingSeconds = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Text("STOP TIMER", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { isTimerRunning = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAmber, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Text("START TIMER", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Button(
                onClick = {
                    timeRemainingSeconds = selectedCrop.hours * 3600L
                    isTimerRunning = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrightAmber, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Text("START TIMER", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
