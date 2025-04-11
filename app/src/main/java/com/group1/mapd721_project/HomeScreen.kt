package com.group1.mapd721_project

import android.Manifest
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "home",
    bluetoothManager: BluetoothManager = BluetoothManager(LocalContext.current)
) {

    val context = LocalContext.current
    val connectedPillboxes = listOf("Pillbox A")
    val availablePillboxes = remember { mutableStateListOf("Pillbox A", "Pillbox B", "Pillbox C") } // Simulated list

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

    // Handle pillbox connection
    LaunchedEffect(pillboxToConnect) {
        if (pillboxToConnect != null) {
            isConnecting = true
            delay(2000) // Simulate connection time
            isConnecting = false
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
    // Connection Progress Dialog
    if (isConnecting) {
        AlertDialog(
            onDismissRequest = { /* Don't allow dismiss while connecting */ },
            title = {
                Text("Connecting...")
            },
            text = {
                Column {
                    Text("Connecting to selected pillbox...")
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {}
        )
    }


    val connectedPillboxes = listOf("Pillbox A")
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

            Text(
                text = "Connected Pillboxes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            connectedPillboxes.forEach { pillbox ->
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