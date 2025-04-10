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
    var selectedDate by remember { mutableStateOf("Today") }
    val dateOptions = remember {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val today = LocalDate.now()
        val days = listOf(0, 1, 2, 3, 4)

        days.map { offset ->
            val date = today.plusDays(offset.toLong())
            when (offset) {
                0 -> "Today"
                1 -> "Tomorrow "
                else -> date.format(formatter)
            }
        }
    }

    val reminders = listOf(
        Triple("ASCAP tablets", "123 mg", false),
        Triple("Vitamin D", "500 IU", false),
        Triple("Lisinopril", "10 mg", false)
    )

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
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(onClick = { /* TODO: Calendar click */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.today),
                                contentDescription = null // decorative element
                            )
                        }
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

            // Date Tabs (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                dateOptions.forEach { date ->
                    Text(
                        text = date,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { selectedDate = date }
                            .background(
                                if (selectedDate == date) Color.LightGray else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reminders.size) { index ->
                    val (name, dose, _) = reminders[index]
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.Gray) // Placeholder for pill image
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = name, style = MaterialTheme.typography.titleMedium)
                                Text(text = dose, style = MaterialTheme.typography.bodySmall)
                            }

                            Checkbox(
                                checked = false,
                                onCheckedChange = { /* Optional: update state */ }
                            )
                        }
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