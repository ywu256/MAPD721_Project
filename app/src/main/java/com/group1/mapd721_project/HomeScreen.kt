package com.group1.mapd721_project

import android.Manifest
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

import kotlinx.coroutines.delay

// Create a companion object to store the pillbox state
object PillboxStateManager {
    val connectedPillboxes = mutableStateListOf<String>()
    val availablePillboxes = mutableStateListOf<String>()

    // Initialize with default available pillboxes if not already initialized
    fun initializeIfNeeded() {
        if (availablePillboxes.isEmpty() && connectedPillboxes.isEmpty()) {
            availablePillboxes.addAll(listOf("Pillbox A", "Pillbox B", "Pillbox C"))
        }
    }
    fun clearConnectedDevices() {
        // Move all connected devices back to available
        availablePillboxes.addAll(connectedPillboxes)
        connectedPillboxes.clear()
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "home",
    bluetoothManager: BluetoothManager = BluetoothManager(LocalContext.current)
) {

    val context = LocalContext.current

    // Initialize the pillbox state if needed
    PillboxStateManager.initializeIfNeeded()

    // Use the shared state for pillboxes
    val connectedPillboxes = remember { PillboxStateManager.connectedPillboxes }
    val availablePillboxes = remember { PillboxStateManager.availablePillboxes }

    // Animation states
    val connectionCompleted = remember { mutableStateOf(false) }
    val animatingPillbox = remember { mutableStateOf<String?>(null) }

    // Bluetooth states
    var showPillboxSelection by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var pillboxToConnect by remember { mutableStateOf<String?>(null) }
    val isBluetoothEnabled by bluetoothManager.isBluetoothEnabled.collectAsState()
    val showBluetoothDialog by bluetoothManager.showBluetoothDialog.collectAsState()

    // Launcher for Bluetooth enable request
    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        bluetoothManager.updateBluetoothState()
        if (isBluetoothEnabled) {
            showPillboxSelection = true
        }
    }

    // Clean up BluetoothManager
    DisposableEffect(bluetoothManager) {
        onDispose {
            bluetoothManager.cleanup()
        }
    }

    // Handle pillbox connection animation
    LaunchedEffect(pillboxToConnect) {
        val currentPillbox = pillboxToConnect
        if (currentPillbox != null) {
            isConnecting = true
            animatingPillbox.value = currentPillbox

            // Simulate connection time
            delay(2000)

            // Connection completed
            isConnecting = false
            connectionCompleted.value = true

            // Move pillbox from available to connected list with animation
            delay(300) // Slight delay before starting the transfer
            if (availablePillboxes.contains(currentPillbox)) {
                availablePillboxes.remove(currentPillbox)
                connectedPillboxes.add(currentPillbox)
            }

            // Reset states
            delay(500)
            connectionCompleted.value = false
            animatingPillbox.value = null
            pillboxToConnect = null
        }
    }

    // Function to handle Bluetooth connection
    val connectBluetooth = {
        if (!isBluetoothEnabled) {
            bluetoothManager.enableBluetooth(bluetoothEnableLauncher)
        } else {
            showPillboxSelection = true
        }
    }

    // Function to initiate pillbox connection
    val connectToPillbox: (String) -> Unit = { pillbox ->
        showPillboxSelection = false
        pillboxToConnect = pillbox
    }

    // Bluetooth Enable Dialog
    if (showBluetoothDialog) {
        AlertDialog(
            onDismissRequest = {
                bluetoothManager.dismissDialog()
            },
            title = {
                Text("Bluetooth Settings")
            },
            text = {
                Text("Please turn on Bluetooth in settings to connect to your pillbox")
            },
            confirmButton = {
                Button(
                    onClick = {
                        bluetoothManager.openBluetoothSettings()
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { bluetoothManager.dismissDialog() }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Pillbox Selection Dialog
    if (showPillboxSelection) {
        AlertDialog(
            onDismissRequest = {
                showPillboxSelection = false
            },
            title = {
                Text("Select Pillbox")
            },
            text = {
                Column {
                    availablePillboxes.forEach { pillbox ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { connectToPillbox(pillbox) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = pillbox,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPillboxSelection = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Connection Progress Dialog with Animation
    if (isConnecting) {
        AlertDialog(
            onDismissRequest = { /* Don't allow dismiss while connecting */ },
            title = {
                Text("Connecting...")
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Use null-safe access for pillboxToConnect
                    val currentPillbox = pillboxToConnect
                    Text("Connecting to ${currentPillbox ?: "selected pillbox"}...")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated progress indicator
                    val progressAnimation = remember { mutableStateOf(0f) }

                    LaunchedEffect(isConnecting) {
                        // Animate from 0 to 1 over the connection duration
                        val steps = 100
                        val stepDuration = 2000 / steps
                        for (i in 1..steps) {
                            progressAnimation.value = i.toFloat() / steps
                            delay(stepDuration.toLong())
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progressAnimation.value },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Show animation completion message
                    AnimatedVisibility(
                        visible = connectionCompleted.value,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Connected",
                                tint = Color.Green
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connected successfully!")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(64.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            "Welcome back",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = { /* TODO: Notification click */ }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = connectBluetooth
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bluetooth),
                    contentDescription = "Connect Bluetooth"
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { /* stay */ },
                    icon = { Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home") },
                    label = { Text("Home", fontSize = 16.sp) }
                )
                NavigationBarItem(
                    selected = currentRoute == "medication_list",
                    onClick = { onNavigate("medication_list") },
                    icon = { Icon(painter = painterResource(id = R.drawable.medication), contentDescription = "Medication") },
                    label = { Text("Medications", fontSize = 16.sp) }
                )
                NavigationBarItem(
                    selected = currentRoute == "settings",
                    onClick = { onNavigate("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 16.sp) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Available Pillboxes Section
            Text(
                text = "Available Pillboxes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (availablePillboxes.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No available pillboxes",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                availablePillboxes.forEach { pillbox ->
                    // Skip animation for the currently animating pillbox
                    if (pillbox != animatingPillbox.value) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                                    slideInVertically(
                                        animationSpec = tween(durationMillis = 500)
                                    ),
                            exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                                    slideOutVertically(
                                        animationSpec = tween(durationMillis = 300)
                                    )
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { /* Add navigation */ },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = pillbox,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connected Pillboxes Section
            Text(
                text = "Connected Pillboxes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (connectedPillboxes.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "No pillboxes connected",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                connectedPillboxes.forEach { pillbox ->
                    val animatedState = remember { MutableTransitionState(false).apply { targetState = true } }

                    AnimatedVisibility(
                        visibleState = animatedState,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                                slideInVertically(
                                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                                ) { height -> -height },
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pillbox,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onNavigate = {},
            currentRoute = "home"
        )
    }
}