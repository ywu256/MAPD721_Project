package com.group1.mapd721_project
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    modifier: Modifier = Modifier,
    onAddMedicationClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}, // for navController.navigate
    currentRoute: String = "medication_list",
    medicineDataStore: MedicineDataStore = MedicineDataStore(LocalContext.current)
) {
    var medications by remember { mutableStateOf<List<MedicineModel>>(emptyList()) }
    LaunchedEffect(Unit) {
        medicineDataStore.getMedicine.collect { medicineList ->
            for (med in medicineList) {
            }
            medications = medicineList
        }
    }

    var searchQuery by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Medication") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                shape = RoundedCornerShape(20.dp)
            )

            val filteredMedications = medications.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }

            if (filteredMedications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No medications yet.\nTap + to add one.", textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMedications) { med ->
                        MedicationCard(med = med, onNavigate = onNavigate)
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationCard(med: MedicineModel, onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                onNavigate("medication_detail/${med.name.replace(" ", "_")}")
            },
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
            Text(
                text = "${med.name} (${med.format})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.repeat),
                    contentDescription = "Frequency",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                val frequencyText = when(med.frequency) {
                    Frequency.DAILY -> "Daily"
                    Frequency.WEEKLY -> {
                        val days = med.daysOfWeek.joinToString(", ")
                        "On $days"
                    }
                    Frequency.INTERVAL -> "Every ${med.interval} days"
                }
                Text("Frequency: $frequencyText", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))

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