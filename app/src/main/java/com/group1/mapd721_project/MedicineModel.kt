package com.group1.mapd721_project

import java.util.UUID

data class MedicineModel(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val time: String,
    val frequency: Frequency,
    val format: String = "",
    val dosage: String = "",
    val daysOfWeek: List<DaysOfWeek> = emptyList(),
    val interval: Int = 0
)

enum class Frequency {
    DAILY,
    WEEKLY,
    INTERVAL
}

enum class DaysOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
