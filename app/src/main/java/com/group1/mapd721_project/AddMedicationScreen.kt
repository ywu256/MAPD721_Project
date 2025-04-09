package com.group1.mapd721_project
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // initially selecred the first item of the list
    var selectedFrequency by remember { mutableStateOf(frequency[0]) }
    var isExpanded by remember { mutableStateOf(false) }
    val (selectedFormat, onOptionSelected) = remember { mutableStateOf(formats[0]) }
    var medicationName by remember { mutableStateOf("") }
    var medicationDosage by remember { mutableStateOf("") }
    var medicationDuration by remember { mutableStateOf("") }
    // added a dependicies to use java date and time library
    var remainderTime by remember { mutableStateOf(LocalTime.now()) }


    Column(
        modifier = Modifier
            //.background(Color.LightGray)
            .verticalScroll(rememberScrollState())
    ) {
//        repeat(10) {
//            Text("Item $it", modifier = Modifier.padding(2.dp))
//        }
        Text("Medicine Name:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        OutlinedTextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Medication Name") },
            placeholder = { Text("Please input your ID") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Formats:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Column(Modifier.selectableGroup()) {
            formats.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(6.dp)
                        .selectable(
                            selected = (text == selectedFormat),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    RadioButton(
                        selected = (text == selectedFormat),
                        onClick = null
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            // Text(selectedFormat) used to test selectedFormat later
            // resume from here, inside of the Column
            Spacer(modifier = Modifier.height(5.dp))
            Text("Dosage:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            OutlinedTextField(
                value = medicationDosage,
                onValueChange = { medicationDosage = it },
                label = { Text("Dosage") },
                placeholder = { Text("Please input your ID") })
            Spacer(modifier = Modifier.height(10.dp))
            // which time to take the medication
            Text("Frequency:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Column (
                modifier = Modifier.fillMaxWidth()
            ){
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded },
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        value = selectedFrequency,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
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
            }
            // test selected frequency
            // add more elements inside the column
            Text("currently selected: " + selectedFrequency)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }


}



@Preview(showBackground = true)
@Composable
fun AddMedicineScreePreview() {
    AddMedicineScreen()
}
