package com.group1.mapd721_project
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen() {
    // medicine format list
    val formats = listOf("Capsule", "Tablet", "Liquid", "Topical", "Others")
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
    var remainderTime by remember { mutableStateOf(LocalTime.now()) }

    val horizontalPadding = 16.dp
    val verticalSpacing = 16.dp
    val sectionSpacing = 24.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Medication") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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

                    }
                    "Every Few Days" -> {
                        Spacer(modifier = Modifier.height(verticalSpacing))
                        Text("Interval (days):", fontSize = 16.sp)
                        // add inverval choisce radio or dropdown box
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    AddMedicineScreen()
}