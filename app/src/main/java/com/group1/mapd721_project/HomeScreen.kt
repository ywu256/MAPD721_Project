package com.group1.mapd721_project

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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Create a companion object to store the pillbox state
object PillboxStateManager {
    val connectedPillboxes = mutableStateListOf<String>()
    val availablePillboxes = mutableStateListOf<String>()
    val pillboxToMedicationMap = mutableStateMapOf<String, MedicineModel>()

    // Initializing with default available pillboxes
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

    // Animation states for disconnecting
    val disconnectingPillbox = remember { mutableStateOf<String?>(null) }
    val isDisconnecting = remember { mutableStateOf(false) }
    val disconnectionCompleted = remember { mutableStateOf(false) }

    // Bluetooth states
    var showPillboxSelection by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var pillboxToConnect by remember { mutableStateOf<String?>(null) }
    val isBluetoothEnabled by bluetoothManager.isBluetoothEnabled.collectAsState()
    val showBluetoothDialog by bluetoothManager.showBluetoothDialog.collectAsState()

    // State for disconnect confirmation dialog
    var showDisconnectDialog by remember { mutableStateOf(false) }
    var pillboxToDisconnect by remember { mutableStateOf<String?>(null) }

    // State for medicine selection dialog
    val showMedicationDialog = remember { mutableStateOf(false) }
    val selectedMedication = remember { mutableStateOf<MedicineModel?>(null) }
    val medicineDataStore = remember { MedicineDataStore(context) }
    val medications by medicineDataStore.getMedicine.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

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

    // Handle pillbox disconnection animation
    LaunchedEffect(disconnectingPillbox.value) {
        val pillbox = disconnectingPillbox.value
        if (pillbox != null) {
            isDisconnecting.value = true
            // Simulate disconnection time
            delay(1500)
            // Mark disconnection as complete
            disconnectionCompleted.value = true
            delay(1000)
            // Move pillbox from connected to available
            if (connectedPillboxes.contains(pillbox)) {
                connectedPillboxes.remove(pillbox)
                availablePillboxes.add(pillbox)
            }
            // Reset states
            delay(500)
            isDisconnecting.value = false
            disconnectionCompleted.value = false
            disconnectingPillbox.value = null
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
        showMedicationDialog.value = true
    }

    // Function to initiate pillbox disconnection
    val initiateDisconnect: (String) -> Unit = { pillbox ->
        pillboxToDisconnect = pillbox
        showDisconnectDialog = true
    }

    // Function to confirm disconnection
    val confirmDisconnect: () -> Unit = {
        pillboxToDisconnect?.let { pillbox ->
            showDisconnectDialog = false
            disconnectingPillbox.value = pillbox
            pillboxToDisconnect = null
            PillboxStateManager.pillboxToMedicationMap.remove(pillbox)
        }
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

    // Disconnect Confirmation Dialog
    if (showDisconnectDialog) {
        AlertDialog(
            onDismissRequest = { showDisconnectDialog = false },
            title = { Text("Disconnect Pillbox") },
            text = { Text("Do you want to disconnect from ${pillboxToDisconnect}?") },
            confirmButton = {
                Button(
                    onClick = confirmDisconnect
                ) {
                    Text("Yes, Disconnect")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDisconnectDialog = false }
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

    // Disconnection Progress Dialog
    if (isDisconnecting.value) {
        AlertDialog(
            onDismissRequest = { /* Don't allow dismiss while disconnecting */ },
            title = { Text("Disconnecting...") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentPillbox = disconnectingPillbox.value
                    Text("Disconnecting from ${currentPillbox ?: "pillbox"}...")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated progress indicator
                    val progressAnimation = remember { mutableStateOf(0f) }

                    LaunchedEffect(isDisconnecting.value) {
                        // Animate from 0 to 1 over the disconnection duration
                        val steps = 100
                        val stepDuration = 1500 / steps
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
                        visible = disconnectionCompleted.value,
                        enter = fadeIn(
                            initialAlpha = 0f,
                            animationSpec = tween(durationMillis = 500)
                        ) + slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(durationMillis = 500)
                        ),
                        exit = fadeOut(
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Disconnected",
                                tint = Color.Green
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Disconnected successfully!")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // Display medicine dialog
    if (showMedicationDialog.value) {
        AlertDialog(
            onDismissRequest = { showMedicationDialog.value = false },
            title = { Text("Select Medication for Pillbox") },
            text = {
                Column {
                    // Only displays medicine that hasn't used
                    val usedMedications = PillboxStateManager.pillboxToMedicationMap.values.toSet()
                    val availableMedications = medications.filterNot { it in usedMedications }

                    availableMedications.forEach { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedMedication.value = med
                                    showMedicationDialog.value = false

                                    // Delay to trigger Bluetooth connecting animation
                                    val currentPillbox = pillboxToConnect
                                    if (currentPillbox != null) {
                                        isConnecting = true
                                        animatingPillbox.value = currentPillbox
                                        connectionCompleted.value = false
                                        PillboxStateManager.pillboxToMedicationMap[currentPillbox] = med

                                        // Start animation
                                        scope.launch {
                                            delay(2000)
                                            connectionCompleted.value = true
                                            delay(1000)
                                            isConnecting = false
                                            delay(300)

                                            if (availablePillboxes.contains(currentPillbox)) {
                                                availablePillboxes.remove(currentPillbox)
                                                connectedPillboxes.add(currentPillbox)
                                            }

                                            delay(500)
                                            connectionCompleted.value = false
                                            animatingPillbox.value = null
                                            pillboxToConnect = null
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(
                                text = med.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMedicationDialog.value = false }) {
                    Text("Cancel")
                }
            }
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
                    val boundMedication = PillboxStateManager.pillboxToMedicationMap[pillbox]?.name ?: "No medication assigned"

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
                                .padding(bottom = 8.dp)
                                // Add pointerInput to detect long press
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            initiateDisconnect(pillbox)
                                        }
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = pillbox,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Medication: $boundMedication",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
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