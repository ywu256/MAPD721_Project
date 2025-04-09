package com.group1.mapd721_project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    modifier: Modifier = Modifier,
    onAddMedicationClick: () -> Unit = {}
) {
    val medications = remember {
        mutableStateListOf(
            "Metformin 500mg",
            "Lisinopril 10mg",
            "Atorvastatin 20mg"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Medications") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMedicationClick) {
                Text("+")
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
                Text("No medications yet.\nTap + to add one.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = med, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}