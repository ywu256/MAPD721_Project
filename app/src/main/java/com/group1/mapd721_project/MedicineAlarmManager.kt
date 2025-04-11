package com.group1.mapd721_project

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class MedicineAlarmManager(
    private val context: Context,
) {
    fun scheduleMedicineReminder(medicine: MedicineModel) {
        when (medicine.frequency){
            Frequency.DAILY -> {
                scheduleDailyReminder(medicine)
            }
            Frequency.WEEKLY -> {
                scheduleWeeklyReminder(medicine)
            }
            Frequency.INTERVAL -> {
                scheduleIntervalReminder(medicine)
            }
        }
    }
    fun scheduleDailyReminder(medicine: MedicineModel) {
        scheduleAlarm(medicine, getTime(medicine.time))
    }
    fun scheduleWeeklyReminder(medicine: MedicineModel) {
        // has a list of day
        medicine.daysOfWeek.forEach{ day ->
            val cal = getTime(medicine.time)
            // set the date to calender formate (int number)
            cal.set(Calendar.DAY_OF_WEEK, day.convertDay())
            if (cal.before(Calendar.getInstance())){
                cal.add(Calendar.WEEK_OF_YEAR, 1)
            }
            scheduleAlarm(medicine, cal, day.ordinal)

        }
    }
    fun scheduleIntervalReminder(medicine: MedicineModel) {
        // has interval
        val cal = getTime(medicine.time)
        if (cal.before(Calendar.getInstance())){
            cal.add(Calendar.DATE, medicine.interval)
        }
        scheduleAlarm(medicine, cal)
    }
    private fun DaysOfWeek.convertDay(): Int = when (this) {
        DaysOfWeek.MONDAY -> Calendar.MONDAY
        DaysOfWeek.TUESDAY -> Calendar.TUESDAY
        DaysOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
        DaysOfWeek.THURSDAY -> Calendar.THURSDAY
        DaysOfWeek.FRIDAY -> Calendar.FRIDAY
        DaysOfWeek.SATURDAY -> Calendar.SATURDAY
        DaysOfWeek.SUNDAY -> Calendar.SUNDAY

    }
    private fun getTime(time: String): Calendar {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = formatter.parse(time)
        return Calendar.getInstance().apply {
            if (date != null) {
                setTime(date)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                val now = Calendar.getInstance()
                if (before(now)) {
                    add(Calendar.DATE, 1)
                }
            }
        }
    }
    private fun scheduleAlarm(medicine: MedicineModel, calendar: Calendar, requestCode: Int = medicine.id.hashCode()) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RemainderReceiver::class.java).apply{
            putExtra("medicine_id", medicine.id.toString())
            putExtra("medicine_name", medicine.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        try{
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("MedicineAlarm", "Scheduled alarm for ${medicine.name} at ${calendar.time}")
        } catch (e: SecurityException){
            Log.e("MedicineAlarm", "Failed to schedule alarm", e)
            e.printStackTrace()
        }
    }

    private fun cancelAlarm(medicine: MedicineModel, calendar: Calendar, requestCode: Int = medicine.id.hashCode()) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RemainderReceiver::class.java).apply{
            putExtra("medicine_id", medicine.id)
            putExtra("medicine_name", medicine.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        try{
            alarmManager.cancel(
                pendingIntent
            )
        } catch (e: SecurityException){
            e.printStackTrace()
        }

    }



}

