package com.group1.mapd721_project

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
//import kotlinx.serialization.json.Json


class MedicineDataStore(private val context: Context) {
    companion object{

        private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore("medicine_data")
        private val ALL_MEDICINE_NAMES = stringPreferencesKey("all_medicine_names")
    }

    // save new medicine
    suspend fun saveMedicine(newMedicine: MedicineModel) {
        context.dataStore.edit { preferences ->
            context.dataStore.edit { preferences ->
                // generate key based on the medicine name
                preferences[stringPreferencesKey("${newMedicine.name}_time")] = newMedicine.time
                preferences[stringPreferencesKey("${newMedicine.name}_frequency")] =
                    newMedicine.frequency.name
                preferences[stringPreferencesKey("${newMedicine.name}_format")] =
                    newMedicine.format ?: ""
                preferences[stringPreferencesKey("${newMedicine.name}_dosage")] =
                    newMedicine.dosage ?: ""
                preferences[stringPreferencesKey("${newMedicine.name}_interval")] =
                    newMedicine.interval.toString()
                // Store days as a comma-separated string
                val daysString = newMedicine.daysOfWeek.joinToString(",") { it.name }
                preferences[stringPreferencesKey("${newMedicine.name}_days")] = daysString
                // Add the name to the list of all medicines
                val existingNamesString = preferences[ALL_MEDICINE_NAMES] ?: ""
                val existingNames =
                    if (existingNamesString.isEmpty()) {
                        emptyList()
                    } else {
                        existingNamesString.split(",")
                    }
                if (!existingNames.contains(newMedicine.name)) {
                    preferences[ALL_MEDICINE_NAMES] =
                        (existingNames + newMedicine.name).joinToString(",")
                }
            }
        }

        // delete medicine
        suspend fun deleteMedicine(medicineName: String) {
            context.dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey("${medicineName}_time"))
                preferences.remove(stringPreferencesKey("${medicineName}_frequency"))
                preferences.remove(stringPreferencesKey("${medicineName}_format"))
                preferences.remove(stringPreferencesKey("${medicineName}_dosage"))
                preferences.remove(stringPreferencesKey("${medicineName}_interval"))
                preferences.remove(stringPreferencesKey("${medicineName}_days"))
                // Remove the name from the list of all medicines
                val existingNamesString = preferences[ALL_MEDICINE_NAMES] ?: ""
                val existingNames =
                    if (existingNamesString.isEmpty()) {
                        emptyList()
                    } else {
                        existingNamesString.split(",")
                    }
                if (existingNames.contains(medicineName)) {
                    val updatedMedicine = existingNames.filter { it != medicineName }
                    preferences[ALL_MEDICINE_NAMES] = updatedMedicine.joinToString(",")
                }
            }
        }
        // get all medicines
        val getMedicine: Flow<List<MedicineModel>> = context.dataStore.data.map { preferences ->
            val allMedicineNames = preferences[ALL_MEDICINE_NAMES] ?: ""
            if (allMedicineNames.isEmpty()) {
                emptyList()
            } else {
                allMedicineNames.split(",").mapNotNull { medicineName ->
                    val time = preferences[stringPreferencesKey("${medicineName}_time")] ?: ""
                    val frequency =
                        preferences[stringPreferencesKey("${medicineName}_frequency")] ?: ""
                    val format = preferences[stringPreferencesKey("${medicineName}_format")] ?: ""
                    val dosage = preferences[stringPreferencesKey("${medicineName}_dosage")] ?: ""
                    val interval =
                        preferences[stringPreferencesKey("${medicineName}_interval")]?.toIntOrNull()
                            ?: 0
                    val daysString = preferences[stringPreferencesKey("${medicineName}_days")] ?: ""
                    val daysOfWeek = daysString.split(",").map {
                        DaysOfWeek.valueOf(it)
                    }
                    MedicineModel(
                        name = medicineName,
                        time = time,
                        frequency = Frequency.valueOf(frequency),
                        format = format,
                        dosage = dosage,
                        daysOfWeek = daysOfWeek,
                        interval = interval
                    )
                }
            }
        }


        fun getMedicineByname(medicineName: String): Flow<MedicineModel?> {
            return context.dataStore.data.map { preferences ->
                val time = preferences[stringPreferencesKey("${medicineName}_time")] ?: ""
                val frequency =
                    preferences[stringPreferencesKey("${medicineName}_frequency")] ?: ""
                val format = preferences[stringPreferencesKey("${medicineName}_format")] ?: ""
                val dosage = preferences[stringPreferencesKey("${medicineName}_dosage")] ?: ""
                val interval =
                    preferences[stringPreferencesKey("${medicineName}_interval")]?.toIntOrNull()
                        ?: 0
                val daysString = preferences[stringPreferencesKey("${medicineName}_days")] ?: ""
                val daysOfWeek = daysString.split(",").map {
                    DaysOfWeek.valueOf(it)
                }
                MedicineModel(
                    name = medicineName,
                    time = time,
                    frequency = Frequency.valueOf(frequency),
                    format = format,
                    dosage = dosage,
                    daysOfWeek = daysOfWeek,
                    interval = interval
                )
            }

        }
    }
}


