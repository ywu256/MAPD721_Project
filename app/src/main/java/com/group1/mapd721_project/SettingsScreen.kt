package com.group1.mapd721_project

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.group1.mapd721_project.ui.theme.MAPD721_ProjectTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "settings",
    userPreferencesManager: UserPreferencesManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize BluetoothManager with cleanup
    val bluetoothManager = remember { BluetoothManager(context) }
    DisposableEffect(bluetoothManager) {
        onDispose {
            bluetoothManager.cleanup()
        }
    }

    // Collect Bluetooth state
    val isBluetoothEnabled by bluetoothManager.isBluetoothEnabled.collectAsState()
    val showBluetoothDialog by bluetoothManager.showBluetoothDialog.collectAsState()

    // Check if Bluetooth is supported
    val isBluetoothSupported = remember { bluetoothManager.isBluetoothSupported() }

    // Bluetooth enable request launcher
    val bluetoothEnableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Update state after activity result
        bluetoothManager.updateBluetoothState()
    }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // Permissions granted, update state
            bluetoothManager.updateBluetoothState()
        } else {
            Toast.makeText(context, "Bluetooth permissions required", Toast.LENGTH_SHORT).show()
        }
    }

    var email by remember { mutableStateOf("") }
    val darkModeEnabled by userPreferencesManager.darkModeFlow.collectAsState(initial = false)
    var showLogoutAlert by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val userData = userPreferencesManager.getUser()
        email = userData.first ?: "Not logged in"
        // Update Bluetooth state on launch
        bluetoothManager.updateBluetoothState()
    }

    MAPD721_ProjectTheme(darkTheme = darkModeEnabled) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Settings",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { onNavigate("home") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 16.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "medication_list",
                        onClick = { onNavigate("medication_list") },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.medication),
                                contentDescription = "Medication"
                            )
                        },
                        label = { Text("Medication", fontSize = 16.sp) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "settings",
                        onClick = { /* stay here */ },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 16.sp) }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Profile section
                Text(
                    text = "Account Info",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = "Person",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = email,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Connectivity
                Text(
                    text = "Connectivity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Bluetooth Item
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bluetooth),
                            contentDescription = "bluetooth",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Bluetooth",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        if (isBluetoothSupported) {
                            Switch(
                                checked = isBluetoothEnabled,
                                onCheckedChange = { checked ->
                                    if (!bluetoothManager.hasBluetoothPermission()) {
                                        permissionLauncher.launch(bluetoothManager.getRequiredPermissions())
                                    } else {
                                        if (checked) {
                                            bluetoothManager.enableBluetooth(bluetoothEnableLauncher)
                                        } else {
                                            bluetoothManager.disableBluetooth()
                                        }
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = "Not supported",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Appearance Section
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    // Dark Mode Item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_dark_mode_24),
                            contentDescription = "DarkMode",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = {
                                scope.launch {
                                    userPreferencesManager.setDarkModeEnabled(it)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // Logout Button
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Button(
                        onClick = { showLogoutAlert = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Logout",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Logout Dialog
                if (showLogoutAlert) {
                    AlertDialog(
                        onDismissRequest = { showLogoutAlert = false },
                        title = { Text("Confirm Logout") },
                        text = { Text("Are you sure you want to logout?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        userPreferencesManager.clearUser()
                                        Toast.makeText(
                                            context,
                                            "Logged out successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onLogout()
                                        showLogoutAlert = false
                                    }
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showLogoutAlert = false }
                            ) {
                                Text("No")
                            }
                        }
                    )
                }

                // Bluetooth Turn Off Dialog
                if (showBluetoothDialog) {
                    AlertDialog(
                        onDismissRequest = { bluetoothManager.dismissDialog() },
                        title = { Text("Turn Off Bluetooth") },
                        text = {
                            Text("On Android 12 and above, apps cannot directly turn off Bluetooth. " +
                                    "Would you like to go to Bluetooth settings to turn it off?")
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
                            Button(
                                onClick = {
                                    bluetoothManager.dismissDialog()
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}