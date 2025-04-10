package com.group1.mapd721_project

import java.util.UUID

data class MedicineModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val time: String,
    val frequency: Frequency,
    val daysOfWeek: List<DaysOfWeek> = emptyList(),
    val interval: Int
)

enum class Frequency {
    DAILY,
    WEEKLY,
    INTERVAL
}
enum class DaysOfWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
}
