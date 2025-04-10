package com.group1.mapd721_project

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MedicationDetail(
    val name: String,
    val dosage: String,
    val nextDosage: String,
    val quantity: String,
    val pillsLeft: String,
    val duration: String,
    val type: String = "Typhoid and malaria tablets for adults"
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MedicationDetails(
    onNavigate: (String) -> Unit = {},
    currentRoute: String = "medication_list",
    medicationId: String = "1", // This would be passed in from the list screen
    onNavigateBack: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val MedicationDetails = remember {
        MedicationDetail(
            name = "Metformin",
            dosage = "500mg",
            nextDosage = "Tomorrow, 8:30AM",
            quantity = "123 pills",
            pillsLeft = "123 pills left",
            duration = "1 month"
        )
    }
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(
                    "Medication Details",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                ) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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
    ){ innerPAdding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPAdding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Card (
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ){
                Column (
                    modifier = Modifier.fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = R.drawable.medication),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${MedicationDetails.name} • ${MedicationDetails.dosage}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = MedicationDetails.type,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Next dosage
            Text("Next Dosage", 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            Card (
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "null",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = MedicationDetails.nextDosage,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Checkbox(
                        checked = false,
                        onCheckedChange = {}
                    )
                }
            }
            // Quantity
            Text("Quantity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Card (
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pill),
                        contentDescription = "null",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = MedicationDetails.quantity,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    VerticalDivider(Modifier.height(25.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = MedicationDetails.pillsLeft,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            // Duration
            Text("Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            Card (
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calender),
                        contentDescription = "null",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = MedicationDetails.duration,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
