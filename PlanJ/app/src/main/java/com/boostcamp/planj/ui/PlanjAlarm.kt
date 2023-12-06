package com.boostcamp.planj.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.boostcamp.planj.data.model.AlarmInfo
import com.boostcamp.planj.data.model.Repetition
import java.util.Calendar

class PlanjAlarm(private val context: Context) {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val alarmIntent by lazy {
        Intent(context, PlanJReceiver::class.java)
    }

    fun setAlarm(alarmInfo: AlarmInfo) {
        with(alarmInfo) {
            val requestCode = scheduleId.hashCode()
            val text = if (alarm.alarmType == "DEPARTURE") {
                "출발 시간 ${alarm.alarmTime}분 전입니다."
            } else {
                "종료 시간 ${alarm.alarmTime}분 전입니다."
            }

            val pendingIntent = createPendingIntent(requestCode, title, text)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = endTime.toMilliseconds()
            calendar.add(Calendar.MINUTE, -alarm.alarmTime - estimatedTime)

            if (repetition == null) {
                setOneTimeAlarm(calendar, pendingIntent)
            } else {
                setRepeatAlarm(calendar, repetition, pendingIntent)
            }
        }
    }

    fun deleteAlarm(requestCode: Int) {
        alarmManager.cancel(createPendingIntent(requestCode))
    }

    private fun setOneTimeAlarm(calendar: Calendar, pendingIntent: PendingIntent) {
        alarmManager.set(
            AlarmManager.RTC,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun setRepeatAlarm(
        calendar: Calendar,
        repetition: Repetition,
        pendingIntent: PendingIntent
    ) {
        val interval = if (repetition.cycleType == "DAILY") {
            repetition.cycleCount
        } else {
            repetition.cycleCount * 7
        }
        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * interval,
            pendingIntent
        )
    }

    private fun createPendingIntent(
        requestCode: Int,
        title: String = "",
        text: String = ""
    ): PendingIntent {
        return alarmIntent.let { intent ->
            intent.putExtra("title", title)
            intent.putExtra("text", text)
            PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}