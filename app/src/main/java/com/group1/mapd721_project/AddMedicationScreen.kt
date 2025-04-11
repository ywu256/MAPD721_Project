package com.group1.mapd721_project
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    currentRoute: String = "medication_list",
    onNavigate: (String) -> Unit = {},
    navController: NavController,
    medicineDataStore: MedicineDataStore = MedicineDataStore(LocalContext.current),
    medicineAlarmManager: MedicineAlarmManager = MedicineAlarmManager(LocalContext.current)
) {
    // medicine format list
    val formats = listOf("Capsule", "Tablet", "Liquid", "Topical", "Others")
    // days of week list
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    // frequency list
    val frequency = listOf("Everyday", "Specific Days of the Week", "Every Few Days")
    // initially selected the first item of the list
    var selectedFrequency by remember { mutableStateOf(frequency[0]) }
    var isExpanded by remember { mutableStateOf(false) }
    val (selectedFormat, onOptionSelected) = remember { mutableStateOf(formats[0]) }
    var medicationName by remember { mutableStateOf("") }
    var medicationDosage by remember { mutableStateOf("") }
    var medicationDuration by remember { mutableStateOf("") }
    // added a dependency to use java date and time library
    val timeDialogState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = false
    )
    val formattedTime by remember {
        derivedStateOf {
            val time = LocalTime.of(timeDialogState.hour, timeDialogState.minute)
            DateTimeFormatter.ofPattern("hh:mm a").format(time)
                 }
    }

    // for interval
    var dayInterval by remember { mutableStateOf("") }

    val horizontalPadding = 16.dp
    val verticalSpacing = 16.dp
    val sectionSpacing = 24.dp
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(
                    "New Medication",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = 8.dp)
            ) {
                // medicine name section
                Text(
                    text = "Medicine Name:",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = verticalSpacing)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = medicationName,
                    onValueChange = { medicationName = it },
                    label = { Text("Medication Name") },
                    placeholder = { Text("Enter medication name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(sectionSpacing))
                // format section
                Text(
                    text = "Format:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                // radio buttons for each format option
                Column(
                    Modifier
                        .selectableGroup()
                        .fillMaxWidth()
                ) {
                    formats.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = (text == selectedFormat),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (text == selectedFormat),
                                onClick = null
                            )
                            Text(
                                text = text,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(sectionSpacing))
                // Dosage Section
                Text(
                    text = "Dosage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = medicationDosage,
                    onValueChange = { medicationDosage = it },
                    label = { Text("Dosage") },
                    placeholder = { Text("Enter dosage amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(sectionSpacing))
                // Frequency Section
                Text(
                    text = "Frequency",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(

                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        frequency.forEach { selected ->
                            DropdownMenuItem(
                                text = { Text(selected) },
                                onClick = {
                                    selectedFrequency = selected
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
                when (selectedFrequency) {
                    "Specific Days of the Week" -> {
                        Spacer(modifier = Modifier.height(verticalSpacing))
                        Text("Days of the week:", fontSize = 16.sp)
                       // display checkboxes for each day of the week
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(5.dp)) {
                            daysOfWeek.forEach { day ->
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Checkbox(
                                        checked = day in selectedDays,
                                        onCheckedChange = { checked ->
                                            selectedDays = if (checked) {
                                                selectedDays + day
                                            } else {
                                                selectedDays - day
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    Text(text = day)
                                }
                                
                            }
                        }

                    }

                    "Every Few Days" -> {
                        Spacer(modifier = Modifier.height(verticalSpacing))
                        Text("Interval (days):", fontSize = 16.sp)
                            OutlinedTextField(
                                value = dayInterval,
                                onValueChange = { dayInterval = it },
                                label = { Text("days") },
                                placeholder = { Text("Every interval days") }
                            )


                    }
                }
                Spacer(modifier = Modifier.height(sectionSpacing))
                Text(
                    text = "Pick Time: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                TimeInput(state = timeDialogState)
                Text(text = formattedTime) //formattedTime works fine
                Spacer(modifier = Modifier.height(sectionSpacing))
                Button(
                    onClick = {
                       CoroutineScope(Dispatchers.IO).launch {
                           val frequency = when(selectedFrequency) {
                               "Everyday" -> Frequency.DAILY
                               "Specific Days of the Week" -> Frequency.WEEKLY
                               "Every Few Days" -> Frequency.INTERVAL
                               else -> Frequency.DAILY
                           }
                           Log.d("MedicineDebug", "Frequency set to: $selectedFrequency")
                           val days = if(selectedFrequency == "Specific Days of the Week") {
                               selectedDays.map {
                                   DaysOfWeek.valueOf(it.uppercase())
                               }
                           } else {
                               emptyList()
                           }
                           val intervalValue = if(selectedFrequency == "Every Few Days") {
                               dayInterval.toIntOrNull() ?: 0
                           } else {
                               0
                           }
                           val newMedicine = MedicineModel(
                               name = medicationName,
                               time = formattedTime,
                               frequency = frequency,
                               format = selectedFormat,
                               dosage = medicationDosage,
                               daysOfWeek = days,
                               interval = intervalValue
                           )
                           medicineDataStore.saveMedicine(newMedicine)
                           medicineAlarmManager.scheduleMedicineReminder(newMedicine)

                           withContext(Dispatchers.Main) {
                               navController.popBackStack()
                           }
                       }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }


            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    val navController = rememberNavController()
    AddMedicineScreen(navController = navController)
}