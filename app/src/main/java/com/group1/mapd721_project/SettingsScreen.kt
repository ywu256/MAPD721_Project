package com.group1.mapd721_project

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
fun SettingsScreen (
    onLogout: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "settings",
    userPreferencesManager: UserPreferencesManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    val darkModeEnabled by userPreferencesManager.darkModeFlow.collectAsState(initial = false)
    var bluetoothEnabled by remember { mutableStateOf(false) }
    var showLogoutAlert by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val userData = userPreferencesManager.getUser()
        email = userData.first ?: "Not logged in"
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
                        Switch(
                            checked = bluetoothEnabled,
                            onCheckedChange = { bluetoothEnabled = it }
                        )
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
                                        )
                                            .show()
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
                Column(modifier = Modifier.padding(16.dp)) {
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
            }
        }
    }
}