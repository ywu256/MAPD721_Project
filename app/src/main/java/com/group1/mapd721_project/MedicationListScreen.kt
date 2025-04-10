package com.group1.mapd721_project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Medication(
    val name: String,
    val format: String,
    val dosage: String,
    val frequency: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    modifier: Modifier = Modifier,
    onAddMedicationClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}, // for navController.navigate
    currentRoute: String = "medication_list"
) {
    // Mock medication data
    val medications = remember {
        mutableStateListOf(
            Medication(
                name = "Metformin",
                format = "Tablet",
                dosage = "500mg",
                frequency = "Everyday",
                time = "08:00 AM"
            ),
            Medication(
                name = "Vitamin D",
                format = "Capsule",
                dosage = "1000 IU",
                frequency = "Specific Days of the Week",
                time = "09:00 AM"
            ),
            Medication(
                name = "Ibuprofen",
                format = "Liquid",
                dosage = "200mg",
                frequency = "Every Few Days",
                time = "07:30 PM"
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(
                    "Medications",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedicationClick) {
                Text("+")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { onNavigate("home") },
                    icon = { Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home") },
                    label = { Text("Home", fontSize = 16.sp) }
                )
                NavigationBarItem(
                    selected = currentRoute == "medication_list",
                    onClick = { /* stay here */ },
                    icon = { Icon(painter = painterResource(id = R.drawable.medication), contentDescription = "Medication") },
                    label = { Text("Medication", fontSize = 16.sp) }
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
        if (medications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No medications yet.\nTap + to add one.", textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { med ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Medication name and type
                            Text(
                                text = "${med.name} (${med.format})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Medication dosage
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.medication),
                                    contentDescription = "Dosage",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dosage: ${med.dosage}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Taken frequency
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.repeat),
                                    contentDescription = "Frequency",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Frequency: ${med.frequency}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Taken time
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.schedule),
                                    contentDescription = "Time",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Time: ${med.time}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}