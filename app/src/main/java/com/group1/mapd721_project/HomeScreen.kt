package com.group1.mapd721_project

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "home"
) {
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
                                contentDescription = null // decorative element
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate("add_pillbox") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Pillbox")
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
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Connected",
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Connected",
                            color = Color.Green,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onNavigate = {},
        currentRoute = "home"
    )
}